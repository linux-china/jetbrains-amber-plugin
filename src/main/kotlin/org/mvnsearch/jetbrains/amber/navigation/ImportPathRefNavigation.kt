package org.mvnsearch.jetbrains.amber.navigation

import com.intellij.navigation.DirectNavigationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import org.mvnsearch.jetbrains.amber.AmberStdLibrary
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
        }
        return null
    }

    private fun withAmberExtension(p: String): String = if (p.endsWith(".ab")) p else "$p.ab"

    companion object {
        private const val STD_PREFIX = "std/"
    }

}