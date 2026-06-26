package org.mvnsearch.jetbrains.commandline

import com.intellij.openapi.fileTypes.LanguageFileType
import org.mvnsearch.jetbrains.amber.AmberIcons
import javax.swing.Icon

object AmberCommandFileType : LanguageFileType(AmberCommandLanguage) {
    override fun getName(): String = "AmberCommand"
    override fun getDescription(): String = "Amber command line file"
    override fun getDefaultExtension(): String = "abc"
    override fun getIcon(): Icon = AmberIcons.FILE
}
