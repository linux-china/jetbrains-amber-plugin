package org.mvnsearch.jetbrains.amber.refactoring

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lexer.FlexAdapter
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import org.mvnsearch.jetbrains.amber.lexer.AmberLexer
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionName
import org.mvnsearch.jetbrains.amber.psi.AmberParameter
import org.mvnsearch.jetbrains.amber.psi.AmberParameterName
import org.mvnsearch.jetbrains.amber.psi.AmberTypes
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitConst
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitMut
import org.mvnsearch.jetbrains.amber.psi.AmberVariableName

class AmberFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner =
        DefaultWordsScanner(
            FlexAdapter(AmberLexer(null)),
            TokenSet.create(AmberTypes.IDENTIFIER),
            TokenSet.create(AmberTypes.LINE_COMMENT, AmberTypes.DOC_COMMENT),
            TokenSet.create(AmberTypes.STRING, AmberTypes.COMMAND)
        )

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean =
        psiElement is AmberFunctionName
                || psiElement is AmberParameterName
                || psiElement is AmberVariableName
                || psiElement is AmberFunctionDef
                || psiElement is AmberParameter
                || psiElement is AmberVariableInitMut
                || psiElement is AmberVariableInitConst

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement): String = when (element) {
        is AmberFunctionName, is AmberFunctionDef -> "function"
        is AmberParameterName, is AmberParameter -> "parameter"
        is AmberVariableName, is AmberVariableInitMut, is AmberVariableInitConst -> "variable"
        else -> ""
    }

    override fun getDescriptiveName(element: PsiElement): String =
        (element as? PsiNamedElement)?.name ?: element.text

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        getDescriptiveName(element)
}
