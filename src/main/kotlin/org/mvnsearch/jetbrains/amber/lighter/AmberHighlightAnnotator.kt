package org.mvnsearch.jetbrains.amber.lighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionCall
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionName
import org.mvnsearch.jetbrains.amber.psi.AmberImportId
import org.mvnsearch.jetbrains.amber.psi.AmberParameterName
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberHighlightAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is AmberFunctionName -> {
                val attributesKey = when (element.parent) {
                    is AmberFunctionDef -> AmberSyntaxHighlighter.FUNCTION_DECLARATION
                    is AmberFunctionCall -> AmberSyntaxHighlighter.FUNCTION_DECLARATION
                    else -> return
                }
                highlight(element, holder, attributesKey)
            }

            is AmberParameterName -> highlight(element, holder, AmberSyntaxHighlighter.PARAMETER)
            is AmberImportId -> highlight(element, holder, AmberSyntaxHighlighter.STATIC_FIELD)
            else -> {
                if (element.elementType == AmberTypes.STRING) {
                    highLightInterpolationInString(element, holder)
                }
            }
        }

    }

    private fun highlight(element: PsiElement, holder: AnnotationHolder, key: TextAttributesKey) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(key)
            .create()
    }

    private fun highLightInterpolationInString(element: PsiElement, holder: AnnotationHolder) {
        val rangeOffset = element.textRange.startOffset
        val text = element.text
        var offset = text.indexOf("{")
        while (offset >= 0) {
            val endOffset = text.indexOf("}", offset + 1)
            if (endOffset > offset) {
                val range = TextRange(rangeOffset + offset, rangeOffset + endOffset + 1)
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(range)
                    .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD).create()
                offset = text.indexOf("{", endOffset + 1)
            } else {
                offset = -1
            }
        }
    }
}