package org.mvnsearch.jetbrains.amber.refactoring

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.mvnsearch.jetbrains.amber.psi.AmberFileElementFactory
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath

class AmberImportPathManipulator : AbstractElementManipulator<AmberImportPath>() {

    override fun getRangeInElement(element: AmberImportPath): TextRange {
        val text = element.text
        return if (text.length >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
            TextRange(1, text.length - 1)
        } else {
            TextRange(0, text.length)
        }
    }

    override fun handleContentChange(
        element: AmberImportPath,
        range: TextRange,
        newContent: String?
    ): AmberImportPath {
        val replacement = newContent ?: ""
        val replaced = range.replace(element.text, replacement)
        val snippet = "import * from $replaced\n"
        val dummy = AmberFileElementFactory.createFile(element.project, snippet)
        val newPath = PsiTreeUtil.findChildOfType(dummy, AmberImportPath::class.java)
            ?: throw IncorrectOperationException("Failed to parse new import path: $replaced")
        return element.replace(newPath) as AmberImportPath
    }
}
