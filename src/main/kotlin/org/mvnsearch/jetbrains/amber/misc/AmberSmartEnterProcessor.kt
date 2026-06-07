package org.mvnsearch.jetbrains.amber.misc

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.mvnsearch.jetbrains.amber.psi.AmberFile

/**
 * Ctrl-Shift-Enter completion for Amber block-opening constructs.
 *
 * Handles two shapes on the current line:
 *   1. `<opener>` with no block: appends ` {\n    \n}` and parks the caret on the
 *      indented blank line. Triggered when the trimmed line starts with one of
 *      `fun` / `pub fun` / `main` / `test` / `if` / `while` / `for` / `loop` /
 *      `else` / `succeeded` / `failed` / `exited`.
 *   2. `<opener> {` (the user has already typed the opening brace): appends
 *      `\n    \n}` to give an indented body line + matching close brace.
 *
 * Detection is text-based on the trimmed current line — the parser can be in a
 * partial state for incomplete constructs, so we avoid leaning on the PSI here.
 * Multiline declarations (e.g., parameter lists wrapped over several lines) are
 * intentionally not handled in this first pass.
 */
class AmberSmartEnterProcessor : SmartEnterProcessor() {

    override fun process(project: Project, editor: Editor, psiFile: PsiFile): Boolean {
        if (psiFile !is AmberFile) return false
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)

        val doc = editor.document
        val caret = editor.caretModel.offset
        val lineNum = doc.getLineNumber(caret)
        val lineStart = doc.getLineStartOffset(lineNum)
        val lineEnd = doc.getLineEndOffset(lineNum)
        val lineText = doc.getText(TextRange(lineStart, lineEnd))
        val trimmedEnd = lineText.trimEnd()
        val content = trimmedEnd.trimStart()
        if (content.isEmpty()) return false

        val indent = lineText.takeWhile { it == ' ' || it == '\t' }
        val bodyIndent = indent + INDENT_UNIT
        val insertOffset = lineStart + trimmedEnd.length

        val (insertText, caretJump) = when {
            content.endsWith("{") -> {
                // `fun foo() {|` — fill in the body line and the closing brace.
                val text = "\n$bodyIndent\n$indent}"
                text to ("\n".length + bodyIndent.length)
            }
            isBlockOpener(content) -> {
                // `fun foo()|` — append ` {\n    \n}`.
                val text = " {\n$bodyIndent\n$indent}"
                text to (" {\n".length + bodyIndent.length)
            }
            else -> return false
        }

        doc.insertString(insertOffset, insertText)
        editor.caretModel.moveToOffset(insertOffset + caretJump)
        return true
    }

    private fun isBlockOpener(content: String): Boolean {
        if (content.endsWith("}")) return false
        val firstWord = content.takeWhile { it != ' ' && it != '(' && it != ':' && it != '?' && it != '{' }
        if (firstWord == "pub") {
            val rest = content.substringAfter("pub ").trimStart()
            return isBlockOpener(rest)
        }
        return firstWord in BLOCK_OPENING_KEYWORDS
    }

    companion object {
        private const val INDENT_UNIT = "    "

        private val BLOCK_OPENING_KEYWORDS = setOf(
            "fun", "main", "test",
            "if", "while", "for", "loop",
            "else", "succeeded", "failed", "exited"
        )
    }
}
