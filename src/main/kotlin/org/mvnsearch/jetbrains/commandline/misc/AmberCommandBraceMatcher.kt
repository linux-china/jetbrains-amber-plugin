package org.mvnsearch.jetbrains.commandline.misc

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.mvnsearch.jetbrains.commandline.psi.AmberCommandTypes

class AmberCommandBraceMatcher : PairedBraceMatcher {

    override fun getPairs(): Array<BracePair> = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        private val PAIRS: Array<BracePair> = arrayOf(
            BracePair(AmberCommandTypes.LBRACE, AmberCommandTypes.RBRACE, false),
            BracePair(AmberCommandTypes.OPEN_PAREN2, AmberCommandTypes.CLOSE_PAREN2, false),
            BracePair(AmberCommandTypes.OPEN_BRACKET2, AmberCommandTypes.CLOSE_BRACKET2, false),
        )
    }
}
