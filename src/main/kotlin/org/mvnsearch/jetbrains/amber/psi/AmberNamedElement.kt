package org.mvnsearch.jetbrains.amber.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

interface AmberNamedElement : PsiNameIdentifierOwner, NavigationItem {

    fun getKey(): String?

    fun getValue(): String?

    override fun getName(): String?

    override fun setName(name: String): PsiElement?

    override fun getNameIdentifier(): PsiElement?

    override fun getPresentation(): ItemPresentation?
}

abstract class AmberNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), AmberNamedElement {
    private var _name: String? = null

    override fun getName(): String? {
        return this._name
    }

    override fun setName(name: String): PsiElement? {
        this._name = name
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return this
    }

    override fun getPresentation(): ItemPresentation? {
        return null
    }

}


