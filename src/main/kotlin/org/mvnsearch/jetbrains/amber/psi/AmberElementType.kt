package org.mvnsearch.jetbrains.amber.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.mvnsearch.jetbrains.amber.AmberLanguage

class AmberElementType(@NonNls debugName: String) : IElementType(debugName, AmberLanguage)
