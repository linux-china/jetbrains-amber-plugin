package org.mvnsearch.jetbrains.commandline.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.mvnsearch.jetbrains.commandline.AmberCommandLanguage

class AmberCommandElementType(@NonNls debugName: String) : IElementType(debugName, AmberCommandLanguage)
