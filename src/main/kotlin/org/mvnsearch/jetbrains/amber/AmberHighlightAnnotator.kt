package org.mvnsearch.jetbrains.amber

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionCall
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionName
import org.mvnsearch.jetbrains.amber.psi.AmberParameterName

class AmberHighlightAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is AmberFunctionName -> {
                val key = when (element.parent) {
                    is AmberFunctionDef -> AmberSyntaxHighlighter.FUNCTION_DECLARATION
                    is AmberFunctionCall -> AmberSyntaxHighlighter.FUNCTION_CALL
                    else -> return
                }
                highlight(element, holder, key)
            }
            is AmberParameterName -> highlight(element, holder, AmberSyntaxHighlighter.PARAMETER)
        }
    }

    private fun highlight(element: PsiElement, holder: AnnotationHolder, key: TextAttributesKey) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(key)
            .create()
    }
}
