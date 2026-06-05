package org.mvnsearch.jetbrains.amber.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.mvnsearch.jetbrains.amber.AmberLanguage

class AmberTokenType(@NonNls debugName: String) : IElementType(debugName, AmberLanguage) {
    override fun toString(): String = "AmberTokenType.${super.toString()}"
}
