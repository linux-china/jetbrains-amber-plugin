package org.mvnsearch.jetbrains.amber.refactoring

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.mvnsearch.jetbrains.amber.psi.AmberCommandLine
import org.mvnsearch.jetbrains.amber.psi.AmberFileElementFactory

/**
 * Applies edits made inside the injected `AmberCommand` document back onto the host
 * `$ command ... $` literal by re-parsing the whole command line.
 */
class AmberCommandLineManipulator : AbstractElementManipulator<AmberCommandLine>() {

    override fun handleContentChange(
        element: AmberCommandLine,
        range: TextRange,
        newContent: String?
    ): AmberCommandLine {
        val replaced = range.replace(element.text, newContent ?: "")
        val dummy = AmberFileElementFactory.createFile(element.project, "$replaced\n")
        val newCommandLine = PsiTreeUtil.findChildOfType(dummy, AmberCommandLine::class.java)
            ?: throw IncorrectOperationException("Failed to parse new command line: $replaced")
        return element.replace(newCommandLine) as AmberCommandLine
    }
}
