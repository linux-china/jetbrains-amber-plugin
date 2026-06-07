package org.mvnsearch.jetbrains.amber.navigation

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import org.mvnsearch.jetbrains.amber.AmberStdLibrary
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath

class AmberImportPathReference(
    element: AmberImportPath,
    range: TextRange
) : PsiReferenceBase<AmberImportPath>(element, range, true) {

    override fun resolve(): PsiElement? {
        val path = element.text.trim('"')
        val project = element.project
        val psiManager = PsiManager.getInstance(project)

        if (path.startsWith(STD_PREFIX)) {
            val virtualFile = AmberStdLibrary.find(path.removePrefix(STD_PREFIX)) ?: return null
            return psiManager.findFile(virtualFile)
        }
        val baseDir = element.containingFile?.originalFile?.virtualFile?.parent ?: return null
        val target = baseDir.findFileByRelativePath(withAmberExtension(path)) ?: return null
        return psiManager.findFile(target)
    }

    override fun isReferenceTo(target: PsiElement): Boolean {
        if (target !is PsiFile) return false
        return resolve() == target
    }

    /**
     * Called by `MoveFileProcessor` and similar refactorings when the referenced file is renamed
     * or moved. Computes the new path text and writes it back via [ElementManipulators].
     *
     * Style is preserved as much as possible:
     *  - same-directory targets keep a leading `./` if the original had one,
     *  - the `.ab` extension is stripped if the original omitted it,
     *  - `std/...` paths are left alone (the std library is read-only).
     */
    override fun bindToElement(newTarget: PsiElement): PsiElement {
        if (newTarget !is PsiFile) return super.bindToElement(newTarget)
        val targetVf = newTarget.virtualFile ?: return super.bindToElement(newTarget)
        val containingDir = element.containingFile?.originalFile?.virtualFile?.parent
            ?: return super.bindToElement(newTarget)

        val oldInner = element.text.trim('"')
        if (oldInner.startsWith(STD_PREFIX)) return element

        var newPath = VfsUtilCore.findRelativePath(containingDir, targetVf, '/')
            ?: return super.bindToElement(newTarget)

        if (oldInner.startsWith("./") && !newPath.startsWith(".")) {
            newPath = "./$newPath"
        }
        if (!oldInner.endsWith(".ab") && newPath.endsWith(".ab")) {
            newPath = newPath.removeSuffix(".ab")
        }

        return ElementManipulators.handleContentChange(element, rangeInElement, newPath)
    }

    private fun withAmberExtension(p: String): String = if (p.endsWith(".ab")) p else "$p.ab"

    companion object {
        private const val STD_PREFIX = "std/"
    }
}
