package org.mvnsearch.jetbrains.amber.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiManager
import org.mvnsearch.jetbrains.amber.AmberFileType
import org.mvnsearch.jetbrains.amber.AmberLanguage
import org.mvnsearch.jetbrains.amber.AmberStdLibrary

class AmberFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, AmberLanguage) {
    override fun getFileType(): FileType = AmberFileType
    override fun toString(): String = "Amber File"

    fun findNamedElement(name: String): AmberNamedElement? {
        return this.children.filterIsInstance<AmberNamedElement>()
            .firstOrNull { it.name == name }
    }

    fun getPubElements(): List<AmberNamedElement> {
        return this.children.filterIsInstance<AmberNamedElement>()
            .filter { it.text.startsWith("pub ") }
    }

    fun getImportedIds(): List<String> {
        val ids = mutableListOf<String>()
        // import * from std or amber file
        this.children.filterIsInstance<AmberImportAll>().forEach {
            val importPath = it.importPath.text.trim('"')
            if (importPath.startsWith("std/")) {
                AmberStdLibrary.findMembers(importPath).forEach { member ->
                    ids.add(member.name)
                }
            } else if (importPath.startsWith("./")) {
                this.virtualFile?.parent?.let { dir ->
                    dir.findChild(importPath.removePrefix("./"))?.let { amberFile ->
                        PsiManager.getInstance(project).findFile(amberFile)?.let { psiFile ->
                            if (psiFile is AmberFile) {
                                psiFile.getPubElements().forEach { pubElement ->
                                    ids.add(pubElement.name!!)
                                }
                            }
                        }
                    }
                }
            }
        }
        // import members
        this.children.filterIsInstance<AmberImportIds>().forEach {
            it.importIdList.forEach { id ->
                ids.add(id.text)
            }
        }
        return ids
    }
}
