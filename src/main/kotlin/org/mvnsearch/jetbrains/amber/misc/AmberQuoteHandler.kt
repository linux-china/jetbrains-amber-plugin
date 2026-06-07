package org.mvnsearch.jetbrains.amber.misc

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.psi.tree.TokenSet
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberQuoteHandler : SimpleTokenSetQuoteHandler(
    TokenSet.create(AmberTypes.STRING)
)
