package org.mvnsearch.jetbrains.amber.structure

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberImportAll
import org.mvnsearch.jetbrains.amber.psi.AmberImportIds
import org.mvnsearch.jetbrains.amber.psi.AmberMainDef
import org.mvnsearch.jetbrains.amber.psi.AmberTestDef
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitConst
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitMut

class AmberStructureViewModel(file: AmberFile, editor: Editor?) :
    StructureViewModelBase(file, editor, AmberStructureViewElement(file)),
    StructureViewModel.ElementInfoProvider {

    init {
        withSuitableClasses(
            AmberVariableInitMut::class.java,
            AmberVariableInitConst::class.java,
            AmberFunctionDef::class.java,
            AmberMainDef::class.java,
            AmberTestDef::class.java,
            AmberImportAll::class.java,
            AmberImportIds::class.java
        )
        withSorters(Sorter.ALPHA_SORTER)
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean =
        element.value !is AmberFile
}
