package org.mvnsearch.jetbrains.commandline.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.mvnsearch.jetbrains.commandline.AmberCommandFileType
import org.mvnsearch.jetbrains.commandline.AmberCommandLanguage

class AmberCommandFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, AmberCommandLanguage) {
    override fun getFileType(): FileType = AmberCommandFileType
    override fun toString(): String = "AmberCommand File"
}
