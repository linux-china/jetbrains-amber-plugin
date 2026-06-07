package org.mvnsearch.jetbrains.amber.navigation

import com.intellij.navigation.DirectNavigationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.mvnsearch.jetbrains.amber.AmberStdLibrary
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionName
import org.mvnsearch.jetbrains.amber.psi.AmberImportAll
import org.mvnsearch.jetbrains.amber.psi.AmberImportIds
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath

@Suppress("UnstableApiUsage")
class ImportPathRefNavigation : DirectNavigationProvider {

    override fun getNavigationElement(element: PsiElement): PsiElement? {
        if (element is AmberImportPath) {
            val path = element.text.trim('"')
            val project = element.project
            val psiManager = PsiManager.getInstance(project)

            if (path.startsWith(STD_PREFIX)) {
                val virtualFile = AmberStdLibrary.find(path.removePrefix(STD_PREFIX)) ?: return null
                return psiManager.findFile(virtualFile)
            }
            val baseDir = element.containingFile?.originalFile?.virtualFile?.parent ?: return null
            val target = baseDir.findFileByRelativePath(withAmberExtension(path)) ?: return null
            val psiFile = psiManager.findFile(target)
            return psiFile
        } else if (element is AmberFunctionName) {
            val functionName = element.text
            val amberPsiFile = element.containingFile as AmberFile
            // find declaration in the file
            val targetElement = amberPsiFile.findNamedElement(functionName)
            if (targetElement != null) {
                return targetElement
            }
            // find declaration from import id list
            amberPsiFile.findChildrenByClass(AmberImportIds::class.java).forEach { importIds ->
                val idList = importIds.importIdList.map { importId -> importId.text }
                if (idList.contains(functionName)) {
                    amberPsiFile.findImportedPsiFile(importIds.importPath.text)?.let { importedPsiFile ->
                        return importedPsiFile.findNamedElement(functionName)
                    }
                }
            }
            // find from import *
            amberPsiFile.findChildrenByClass(AmberImportAll::class.java).forEach { importAll ->
                amberPsiFile.findImportedPsiFile(importAll.importPath.text)?.let { importedPsiFile ->
                    val functionDef = importedPsiFile.findNamedElement(functionName)
                    if (functionDef != null) {
                        return functionDef
                    }
                }
            }
        }
        return null
    }

    private fun withAmberExtension(p: String): String = if (p.endsWith(".ab")) p else "$p.ab"

    companion object {
        private const val STD_PREFIX = "std/"
    }

}