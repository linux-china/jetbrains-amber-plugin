package org.mvnsearch.jetbrains.amber.misc

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.mvnsearch.jetbrains.amber.AmberIcons
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberMainDef
import org.mvnsearch.jetbrains.amber.psi.AmberShebangLine
import javax.swing.Icon

/**
 * Distinguishes executable Amber scripts (those that start with `#!/usr/bin/env amber`)
 * from library `.ab` files in the project view, navigation popups, and editor tabs by
 * overlaying a small "runnable" badge on the standard Amber file icon.
 *
 * Returning `null` for library files lets the underlying `AmberFileType` icon stay in
 * effect, so we don't have to maintain two parallel render paths.
 */
class AmberIconProvider : IconProvider() {

    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val file = element as? AmberFile ?: return null
        return if ((PsiTreeUtil.getChildOfType(file, AmberShebangLine::class.java) != null)
            || (PsiTreeUtil.getChildOfType(file, AmberMainDef::class.java) != null)
        ) {
            AmberIcons.EXECUTABLE_FILE
        } else {
            null
        }
    }
}
