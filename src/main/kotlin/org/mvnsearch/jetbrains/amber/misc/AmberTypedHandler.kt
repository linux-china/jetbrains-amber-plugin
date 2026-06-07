package org.mvnsearch.jetbrains.amber.misc

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberTypedHandler : TypedHandlerDelegate() {

    override fun beforeCharTyped(
        c: Char,
        project: Project,
        editor: Editor,
        file: PsiFile,
        fileType: FileType
    ): Result {
        if (file !is AmberFile || c != '$') return Result.CONTINUE

        val document = editor.document
        val offset = editor.caretModel.offset

        // If the caret sits right before a `$`, swallow the typed `$` and just move the caret past it —
        // mirrors how typing `"` over an existing closing quote works. Stay literal inside strings/comments.
        if (offset < document.textLength
            && document.charsSequence[offset] == '$'
            && !isInsideLiteralContext(editor, offset)
        ) {
            editor.caretModel.moveToOffset(offset + 1)
            return Result.STOP
        }
        return Result.CONTINUE
    }

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is AmberFile || c != '$') return Result.CONTINUE

        val document = editor.document
        val offset = editor.caretModel.offset

        // Skip auto-pair when the typed `$` is inside a string/comment/shebang — it's literal there.
        if (isInsideLiteralContext(editor, offset - 1)) return Result.CONTINUE

        // Don't double-pair if the next character is already `$`.
        if (offset < document.textLength && document.charsSequence[offset] == '$') return Result.CONTINUE

        document.insertString(offset, "$")
        return Result.STOP
    }

    private fun isInsideLiteralContext(editor: Editor, offset: Int): Boolean {
        if (offset < 0) return false
        val editorEx = editor as? EditorEx ?: return false
        val iterator = editorEx.highlighter.createIterator(offset)
        if (iterator.atEnd()) return false
        val type = iterator.tokenType
        return type == AmberTypes.STRING
                || type == AmberTypes.LINE_COMMENT
                || type == AmberTypes.DOC_COMMENT
                || type == AmberTypes.SHEBANG
    }
}
