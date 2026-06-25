package org.mvnsearch.jetbrains.commandline.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.mvnsearch.jetbrains.commandline.AmberCommandLanguage

class AmberCommandTokenType(@NonNls debugName: String) : IElementType(debugName, AmberCommandLanguage) {
    override fun toString(): String = "AmberCommandTokenType.${super.toString()}"
}
