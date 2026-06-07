package org.mvnsearch.jetbrains.amber.structure

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import org.mvnsearch.jetbrains.amber.AmberIcons
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberImportAll
import org.mvnsearch.jetbrains.amber.psi.AmberImportIds
import org.mvnsearch.jetbrains.amber.psi.AmberMainDef
import org.mvnsearch.jetbrains.amber.psi.AmberTestDef
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitConst
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitMut
import javax.swing.Icon

class AmberStructureViewElement(
    private val element: NavigatablePsiElement
) : StructureViewTreeElement, SortableTreeElement {

    override fun getValue(): Any = element

    override fun navigate(requestFocus: Boolean) = element.navigate(requestFocus)

    override fun canNavigate(): Boolean = element.canNavigate()

    override fun canNavigateToSource(): Boolean = element.canNavigateToSource()

    override fun getAlphaSortKey(): String = presentText(element) ?: ""

    override fun getPresentation(): ItemPresentation =
        PresentationData(presentText(element), null, iconFor(element), null)

    override fun getChildren(): Array<TreeElement> {
        if (element !is AmberFile) return TreeElement.EMPTY_ARRAY
        val children = mutableListOf<TreeElement>()
        for (child in element.children) {
            if (child !is NavigatablePsiElement) continue
            when (child) {
                is AmberImportAll,
                is AmberImportIds,
                is AmberVariableInitMut,
                is AmberVariableInitConst,
                is AmberFunctionDef,
                is AmberMainDef,
                is AmberTestDef -> children.add(AmberStructureViewElement(child))
            }
        }
        return children.toTypedArray()
    }

    private fun presentText(element: PsiElement): String? = when (element) {
        is AmberFile -> element.name
        is AmberFunctionDef -> {
            val block = element.block
            element.text.substring(0, block.startOffsetInParent).trim()
        }

        is AmberMainDef -> {
            val block = element.block
            element.text.substring(0, block.startOffsetInParent).trim()
        }

        is AmberTestDef -> {
            val name = element.testName?.text?.trim('"').orEmpty()
            if (name.isEmpty()) "test" else "test \"$name\""
        }

        is AmberImportAll -> "import * from ${element.importPath.text}"
        is AmberImportIds -> "import { … } from ${element.importPath.text}"
        is AmberVariableInitMut,
        is AmberVariableInitConst -> {
            element.text.lineSequence().firstOrNull()?.let { line ->
                return line.substring(0, line.indexOf("=")).trim()
            }
            null
        }

        else -> element.text.lineSequence().firstOrNull()
    }

    private fun iconFor(element: PsiElement): Icon? = when (element) {
        is AmberFile -> AmberIcons.FILE
        is AmberVariableInitMut -> AllIcons.Nodes.Variable
        is AmberVariableInitConst -> AllIcons.Nodes.Variable
        is AmberFunctionDef -> AllIcons.Nodes.Function
        is AmberMainDef -> AllIcons.Nodes.Function
        is AmberTestDef -> AllIcons.Nodes.Test
        is AmberImportAll, is AmberImportIds -> AllIcons.Nodes.Static
        else -> null
    }
}
