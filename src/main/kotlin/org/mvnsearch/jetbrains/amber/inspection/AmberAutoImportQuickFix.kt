package org.mvnsearch.jetbrains.amber.inspection

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFileElementFactory
import org.mvnsearch.jetbrains.amber.psi.AmberImportAll
import org.mvnsearch.jetbrains.amber.psi.AmberImportIds
import org.mvnsearch.jetbrains.amber.psi.AmberShebangLine

/**
 * Quick fix: add `<name>` to a `from "<libPath>"` import.
 *
 * If the file already has `import { … } from "<libPath>"`, the new name is appended
 * to the existing list (deduped). Otherwise a fresh import is inserted at the top
 * of the file, after the shebang and any prior import statements.
 */
class AmberAutoImportQuickFix(
    private val name: String,
    private val libPath: String
) : LocalQuickFix {

    override fun getFamilyName(): String = "Add Amber import"

    override fun getName(): String = "Import '$name' from \"$libPath\""

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement ?: return
        val file = element.containingFile as? AmberFile ?: return
        val psiDocManager = PsiDocumentManager.getInstance(project)
        val document = psiDocManager.getDocument(file) ?: return
        psiDocManager.commitDocument(document)

        val existing = PsiTreeUtil.findChildrenOfType(file, AmberImportIds::class.java)
            .firstOrNull { it.importPath.text.trim('"') == libPath }

        if (existing != null) {
            val existingNames = existing.importIdList.map { it.text.trim() }
            if (name in existingNames) return
            val merged = (existingNames + name).joinToString(", ")
            val newText = "import { $merged } from \"$libPath\""
            val range = existing.textRange
            document.replaceString(range.startOffset, range.endOffset, newText)
        } else {
            val (offset, prefix, suffix) = computeInsertion(file, document)
            document.insertString(offset, prefix + "import { $name } from \"$libPath\"" + suffix)
        }
        psiDocManager.commitDocument(document)
    }

    private fun computeInsertion(file: AmberFile, document: Document): Triple<Int, String, String> {
        val imports = file.children.filter { it is AmberImportIds || it is AmberImportAll }
        val lastImport = imports.maxByOrNull { it.textRange.endOffset }
        if (lastImport != null) {
            return Triple(lastImport.textRange.endOffset, "\n", "")
        }
        val shebang = file.children.filterIsInstance<AmberShebangLine>().firstOrNull()
        if (shebang != null) {
            return Triple(shebang.textRange.endOffset, "\n", "")
        }
        val hasContent = document.textLength > 0
        return Triple(0, "", if (hasContent) "\n\n" else "\n")
    }
}
