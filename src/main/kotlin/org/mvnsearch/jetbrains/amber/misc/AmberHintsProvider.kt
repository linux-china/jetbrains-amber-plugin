package org.mvnsearch.jetbrains.amber.misc

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.mvnsearch.jetbrains.amber.psi.AmberTypes
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitMut


@Suppress("UnstableApiUsage")
class AmberHintsProvider : InlayParameterHintsProvider {
    override fun getParameterHints(element: PsiElement): List<InlayInfo?> {
        val elementType = element.elementType
        if (elementType == AmberTypes.VARIABLE_NAME) {
            if (element.parent is AmberVariableInitMut) {
                val variableInit = element.parent as AmberVariableInitMut
                val variableValue = variableInit.variableValue
                val variableTextValue = element.text
                if (variableValue.string != null) {
                    return listOf(InlayInfo(": Text", element.textRange.endOffset))
                } else if (variableTextValue.equals("true", true) || variableTextValue.equals("false", true)) {
                    return listOf(InlayInfo(": Bool", element.textRange.endOffset))
                } else if (variableTextValue == "null") {
                    return listOf(InlayInfo(": Null", element.textRange.endOffset))
                } else if (variableValue.number != null) {
                    return if (variableTextValue.contains(".")) {
                        listOf(
                            InlayInfo(": Num", element.textRange.endOffset)
                        )
                    } else {
                        listOf(InlayInfo(": Int", element.textRange.endOffset))
                    }
                }
            }
        }
        return super.getParameterHints(element)
    }

    override fun getDefaultBlackList(): Set<String?> {
        return emptySet<String?>()
    }
}