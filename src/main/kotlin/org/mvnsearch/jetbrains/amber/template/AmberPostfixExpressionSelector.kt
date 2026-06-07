package org.mvnsearch.jetbrains.amber.template

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateExpressionSelectorBase
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.Conditions
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.mvnsearch.jetbrains.amber.psi.AmberAdditiveExpression
import org.mvnsearch.jetbrains.amber.psi.AmberAndExpression
import org.mvnsearch.jetbrains.amber.psi.AmberArrayLit
import org.mvnsearch.jetbrains.amber.psi.AmberBooleanLit
import org.mvnsearch.jetbrains.amber.psi.AmberBuiltinsExpression
import org.mvnsearch.jetbrains.amber.psi.AmberComparisonExpression
import org.mvnsearch.jetbrains.amber.psi.AmberExpression
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionCall
import org.mvnsearch.jetbrains.amber.psi.AmberIdentifierRef
import org.mvnsearch.jetbrains.amber.psi.AmberMultiplicativeExpression
import org.mvnsearch.jetbrains.amber.psi.AmberNullLit
import org.mvnsearch.jetbrains.amber.psi.AmberNumberLit
import org.mvnsearch.jetbrains.amber.psi.AmberOrExpression
import org.mvnsearch.jetbrains.amber.psi.AmberParentheses
import org.mvnsearch.jetbrains.amber.psi.AmberPostfixExpression
import org.mvnsearch.jetbrains.amber.psi.AmberRangeExpression
import org.mvnsearch.jetbrains.amber.psi.AmberTextLit
import org.mvnsearch.jetbrains.amber.psi.AmberTypeOpExpression
import org.mvnsearch.jetbrains.amber.psi.AmberUnaryExpression
import com.intellij.util.Function

class AmberPostfixExpressionSelector :
    PostfixTemplateExpressionSelectorBase(Conditions.alwaysTrue<PsiElement>()) {

    override fun getNonFilteredExpressions(
        context: PsiElement,
        document: Document,
        offset: Int
    ): List<PsiElement> {
        // Walk up the PSI chain from the leaf at the caret, keeping the topmost ancestor
        // whose textRange ends EXACTLY at the cursor and that looks like a clean,
        // single-line expression. Stop the moment an ancestor extends past `offset` —
        // higher ancestors only grow, so they can't match either.
        var element: PsiElement? = context
        var topMost: PsiElement? = null
        while (element != null && element !is PsiFile) {
            val range = element.textRange
            if (range.endOffset > offset) break
            if (range.endOffset == offset
                && isExpressionLike(element)
                && isCleanCandidate(element)
            ) {
                topMost = element
            }
            element = element.parent
        }
        return listOfNotNull(topMost?.firstChild)
    }

    override fun getRenderer(): Function<PsiElement, String> = Function { it.text }

    private fun isExpressionLike(element: PsiElement): Boolean =
        element is AmberExpression
                || element is AmberOrExpression
                || element is AmberAndExpression
                || element is AmberComparisonExpression
                || element is AmberRangeExpression
                || element is AmberAdditiveExpression
                || element is AmberMultiplicativeExpression
                || element is AmberTypeOpExpression
                || element is AmberUnaryExpression
                || element is AmberPostfixExpression
                || element is AmberParentheses
                || element is AmberNumberLit
                || element is AmberTextLit
                || element is AmberBooleanLit
                || element is AmberNullLit
                || element is AmberArrayLit
                || element is AmberIdentifierRef
                || element is AmberFunctionCall
                || element is AmberBuiltinsExpression

    /** Reject any candidate whose text crosses a line or includes block braces. */
    private fun isCleanCandidate(element: PsiElement): Boolean {
        val text = element.text
        return !text.contains('\n')
                && !text.contains('{')
                && !text.contains('}')
    }
}
