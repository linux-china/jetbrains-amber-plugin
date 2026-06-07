package org.mvnsearch.jetbrains.amber.misc

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.spellchecker.inspections.PlainTextSplitter
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.TokenConsumer
import com.intellij.spellchecker.tokenizer.Tokenizer
import org.mvnsearch.jetbrains.amber.psi.AmberDocCommentStmt
import org.mvnsearch.jetbrains.amber.psi.AmberTextLit

/**
 * Spell-check only `///` doc-comment lines and the body of `"…"` string literals.
 * Everything else (identifiers, plain `//` comments, command literals, import paths,
 * etc.) returns [EMPTY_TOKENIZER] so the platform's default spell-checking for
 * `PsiNameIdentifierOwner`, `PsiComment`, etc. is suppressed in Amber files.
 */
class AmberSpellcheckingStrategy : SpellcheckingStrategy() {

    override fun getTokenizer(element: PsiElement): Tokenizer<*> = when (element) {
        is PsiComment -> DOC_COMMENT_TOKENIZER
        is AmberTextLit -> TEXT_LIT_TOKENIZER
        else -> EMPTY_TOKENIZER
    }

    companion object {
        private val DOC_COMMENT_TOKENIZER: Tokenizer<PsiComment> =
            object : Tokenizer<PsiComment>() {
                override fun tokenize(element: PsiComment, consumer: TokenConsumer) {
                    val text = element.text
                    val prefix = if (text.startsWith("///")) 3 else 2
                    if (prefix >= text.length) return
                    consumer.consumeToken(
                        element,
                        text,
                        false,
                        0,
                        TextRange(prefix, text.length),
                        PlainTextSplitter.getInstance()
                    )
                }
            }

        private val TEXT_LIT_TOKENIZER: Tokenizer<AmberTextLit> =
            object : Tokenizer<AmberTextLit>() {
                override fun tokenize(element: AmberTextLit, consumer: TokenConsumer) {
                    val text = element.text
                    if (text.length < 2 || !text.startsWith("\"")) return
                    val end = if (text.endsWith("\"")) text.length - 1 else text.length
                    if (end <= 1) return
                    consumer.consumeToken(
                        element,
                        text,
                        false,
                        0,
                        TextRange(1, end),
                        PlainTextSplitter.getInstance()
                    )
                }
            }
    }
}
