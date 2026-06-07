package org.mvnsearch.jetbrains.amber

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.LayeredIcon
import javax.swing.Icon

object AmberIcons {
    @JvmField
    val FILE: Icon = IconLoader.getIcon("/icons/amber-16x16.svg", AmberIcons::class.java)

    @JvmField
    val EXECUTABLE_FILE: Icon = LayeredIcon(2).apply {
        setIcon(FILE, 0)
        setIcon(AllIcons.Nodes.RunnableMark, 1)
    }
}
