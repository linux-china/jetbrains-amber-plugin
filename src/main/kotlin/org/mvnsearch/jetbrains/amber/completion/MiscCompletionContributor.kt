package org.mvnsearch.jetbrains.amber.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.mvnsearch.jetbrains.amber.AmberLanguage
import org.mvnsearch.jetbrains.amber.psi.AmberAttribute
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberImportAll
import org.mvnsearch.jetbrains.amber.psi.AmberImportIds
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath
import org.mvnsearch.jetbrains.amber.psi.AmberNamedElement
import org.mvnsearch.jetbrains.amber.psi.AmberTypeRef
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class MiscCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            psiElement(PsiElement::class.java)
                .withElementType(AmberTypes.IDENTIFIER)
                .withLanguage(AmberLanguage),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val position = parameters.position

                    // Imports and attribute names use their own identifier sets — skip here.
                    if (isInsideImport(position)) return
                    if (isInsideAttribute(position)) return
                    // Skip when the user is typing a postfix-template trigger (e.g. `expr.if`):
                    // the postfix template provider handles those abbreviations.
                    if (isPostfixContext(parameters)) return

                    // Inside a type annotation (`: Text`, `as Num`, `is Bool`) only built-in types make sense.
                    if (isInsideTypeRef(position)) {
                        TYPE_NAMES.forEach { add(result, it) }
                        return
                    }

                    GENERAL_KEYWORDS.forEach { add(result, it) }
                    BUILTIN_NAMES.forEach { add(result, it) }
                    LITERALS.forEach { add(result, it) }
                    val amberPsiFile = position.containingFile as AmberFile
                    // imported ids
                    amberPsiFile.getImportedIds().forEach { idName ->
                        add(result, idName)
                    }
                    // declared functions or variables
                    amberPsiFile.findChildrenByClass(AmberNamedElement::class.java)
                        .forEach { functionDef -> add(result, functionDef.name!!) }
                }
            }
        )
    }

    private fun add(result: CompletionResultSet, keyword: String) {
        result.addElement(LookupElementBuilder.create(keyword).bold())
    }

    private fun isInsideImport(element: PsiElement): Boolean =
        PsiTreeUtil.getParentOfType(element, AmberImportIds::class.java) != null ||
                PsiTreeUtil.getParentOfType(element, AmberImportAll::class.java) != null ||
                PsiTreeUtil.getParentOfType(element, AmberImportPath::class.java) != null

    private fun isInsideAttribute(element: PsiElement): Boolean =
        PsiTreeUtil.getParentOfType(element, AmberAttribute::class.java) != null

    private fun isInsideTypeRef(element: PsiElement): Boolean =
        PsiTreeUtil.getParentOfType(element, AmberTypeRef::class.java) != null

    /**
     * Detect `expression.<typing>` postfix-template positions by inspecting the character
     * immediately preceding the IDENTIFIER being typed. The platform inserts a synthetic
     * dummy identifier at the caret, so its text range start sits right where the user is
     * typing — if the previous char is `.`, this is a postfix trigger, not a free-standing
     * keyword position.
     */
    private fun isPostfixContext(parameters: CompletionParameters): Boolean {
        val startOffset = parameters.position.textRange.startOffset
        if (startOffset <= 0) return false
        val text = parameters.editor.document.charsSequence
        return text[startOffset - 1] == '.'
    }

    companion object {
        private val GENERAL_KEYWORDS = listOf(
            // Control flow & declarations
            "if", "else", "then", "while", "for", "loop", "in",
            "let", "const", "return", "fail", "break", "continue",
            "fun", "pub", "ref", "main", "test", "import", "from", "as", "is",
            // Boolean / cast / null operators that read as words
            "and", "or", "not",
            // Command modifiers and handlers
            "silent", "suppress", "trust", "sudo", "unsafe", "await",
            "succeeded", "failed", "exited"
        )

        private val BUILTIN_NAMES = listOf(
            // Statement-style builtins
            "echo", "cd", "clear", "cp", "disown", "exit", "lock", "mv", "rm", "sleep", "touch",
            // Expression-style builtins
            "len", "lines", "ls", "nameof", "pid", "pwd", "shellname", "shellversion", "status"
        )

        private val LITERALS = listOf("true", "false", "null")

        private val TYPE_NAMES = listOf("Text", "Num", "Bool", "Null", "Int")
    }
}
