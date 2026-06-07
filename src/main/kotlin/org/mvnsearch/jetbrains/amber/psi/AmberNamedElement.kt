package org.mvnsearch.jetbrains.amber.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import javax.swing.Icon

interface AmberNamedElement : PsiNameIdentifierOwner, NavigationItem {

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
        val presentationText = this.name ?: return null
        val element = this
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return presentationText
            }

            override fun getLocationString(): String? {
                return element.containingFile?.name
            }

            override fun getIcon(unused: Boolean): Icon? {
                return null
            }
        }
    }

}


abstract class AmberFunctionDefElementImpl(node: ASTNode) : AmberNamedElementImpl(node) {

    override fun getName(): String? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.FUNCTION_NAME)
        return keyNode?.text
    }

    override fun setName(name: String): PsiElement? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.FUNCTION_NAME)
        if (keyNode != null) {
            val functionDef = AmberFileElementFactory.createFunctionDef(this.project, name)
            val newKeyNode = functionDef.functionName.node
            this.node.replaceChild(keyNode, newKeyNode)
        }
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return this.node.findChildByType(AmberTypes.FUNCTION_NAME)?.psi
    }
}

abstract class AmberVariableInitConstElementImpl(node: ASTNode) : AmberNamedElementImpl(node) {

    override fun getName(): String? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        return keyNode?.text
    }

    override fun setName(name: String): PsiElement? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        if (keyNode != null) {
            val constVariable = AmberFileElementFactory.createAmberVariableInitConst(this.project, name)
            val newKeyNode = constVariable.variableName.node
            this.node.replaceChild(keyNode, newKeyNode)
        }
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return this.node.findChildByType(AmberTypes.VARIABLE_NAME)?.psi
    }

}

abstract class AmberVariableInitMutElementImpl(node: ASTNode) : AmberNamedElementImpl(node) {

    override fun getName(): String? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        return keyNode?.text
    }

    override fun setName(name: String): PsiElement? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        if (keyNode != null) {
            val mutVariable = AmberFileElementFactory.createAmberVariableInitMut(this.project, name)
            val newKeyNode = mutVariable.variableName.node
            this.node.replaceChild(keyNode, newKeyNode)
        }
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return this.node.findChildByType(AmberTypes.VARIABLE_NAME)?.psi
    }

}

abstract class AmberFunctionNameElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PsiNameIdentifierOwner {

    override fun getName(): String = text

    override fun setName(name: String): PsiElement {
        val replacement = AmberFileElementFactory.createFunctionName(project, name)
        return this.replace(replacement)
    }

    override fun getNameIdentifier(): PsiElement? = firstChild
}

abstract class AmberVariableNameElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PsiNameIdentifierOwner {

    override fun getName(): String = text

    override fun setName(name: String): PsiElement {
        val replacement = AmberFileElementFactory.createVariableName(project, name)
        return this.replace(replacement)
    }

    override fun getNameIdentifier(): PsiElement? = firstChild
}

abstract class AmberParameterNameElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    PsiNameIdentifierOwner {

    override fun getName(): String = text

    override fun setName(name: String): PsiElement {
        val replacement = AmberFileElementFactory.createParameterName(project, name)
        return this.replace(replacement)
    }

    override fun getNameIdentifier(): PsiElement? = firstChild
}

