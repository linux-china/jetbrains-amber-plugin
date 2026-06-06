package org.mvnsearch.jetbrains.amber.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import javax.swing.Icon

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
        val presentationText = this.getKey()
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
    override fun getKey(): String? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.FUNCTION_NAME)
        return keyNode?.text
    }

    override fun getValue(): String? {
        val valueNode: ASTNode? = this.node.findChildByType(AmberTypes.FUNCTION_NAME)
        return valueNode?.text
    }

    override fun getName(): String? {
        return getKey()
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
    override fun getKey(): String? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        return keyNode?.text
    }

    override fun getValue(): String? {
        val valueNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        return valueNode?.text
    }

    override fun getName(): String? {
        return getKey()
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
    override fun getKey(): String? {
        val keyNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        return keyNode?.text
    }

    override fun getValue(): String? {
        val valueNode: ASTNode? = this.node.findChildByType(AmberTypes.VARIABLE_NAME)
        return valueNode?.text
    }

    override fun getName(): String? {
        return getKey()
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

