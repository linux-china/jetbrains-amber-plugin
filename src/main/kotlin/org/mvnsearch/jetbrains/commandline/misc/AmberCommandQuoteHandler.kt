package org.mvnsearch.jetbrains.commandline.misc

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.psi.tree.TokenSet
import org.mvnsearch.jetbrains.commandline.psi.AmberCommandTypes

class AmberCommandQuoteHandler : SimpleTokenSetQuoteHandler(
    TokenSet.create(AmberCommandTypes.DQ_STRING, AmberCommandTypes.SQ_STRING)
)
