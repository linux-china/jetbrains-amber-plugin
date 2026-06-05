package org.mvnsearch.jetbrains.amber

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberBraceMatcher : PairedBraceMatcher {

    override fun getPairs(): Array<BracePair> = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        private val PAIRS: Array<BracePair> = arrayOf(
            BracePair(AmberTypes.LBRACE, AmberTypes.RBRACE, true),
            BracePair(AmberTypes.LPAREN, AmberTypes.RPAREN, false),
            BracePair(AmberTypes.LBRACK, AmberTypes.RBRACK, false),
            BracePair(AmberTypes.HASH_LBRACK, AmberTypes.RBRACK, false)
        )
    }
}
