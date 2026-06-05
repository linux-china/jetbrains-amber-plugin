package org.mvnsearch.jetbrains.amber

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object AmberFileType : LanguageFileType(AmberLanguage) {
    override fun getName(): String = "Amber"
    override fun getDescription(): String = "Amber language file"
    override fun getDefaultExtension(): String = "ab"
    override fun getIcon(): Icon = AmberIcons.FILE
}
