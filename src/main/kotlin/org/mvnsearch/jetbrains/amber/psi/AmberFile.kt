package org.mvnsearch.jetbrains.amber.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.mvnsearch.jetbrains.amber.AmberFileType
import org.mvnsearch.jetbrains.amber.AmberLanguage

class AmberFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, AmberLanguage) {
    override fun getFileType(): FileType = AmberFileType
    override fun toString(): String = "Amber File"
}
