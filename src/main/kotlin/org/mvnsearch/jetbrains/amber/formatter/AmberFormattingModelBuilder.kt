package org.mvnsearch.jetbrains.amber.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.mvnsearch.jetbrains.amber.AmberLanguage
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val rootBlock = AmberBlock(
            node = formattingContext.node,
            wrap = null,
            alignment = null,
            spacingBuilder = createSpacingBuilder(settings)
        )
        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile,
            rootBlock,
            settings
        )
    }

    private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder =
        SpacingBuilder(settings, AmberLanguage)
            // Keyword-driven spaces — must come BEFORE the broad LPAREN rule so
            // `if (x)`, `while (x)`, etc. keep a space between keyword and `(`.
            .after(AmberTypes.IF_KW).spaces(1)
            .after(AmberTypes.WHILE_KW).spaces(1)
            .after(AmberTypes.FOR_KW).spaces(1)
            .after(AmberTypes.LOOP_KW).spaces(1)
            .after(AmberTypes.LET_KW).spaces(1)
            .after(AmberTypes.CONST_KW).spaces(1)
            .after(AmberTypes.RETURN_KW).spaces(1)
            .after(AmberTypes.FAIL_KW).spaces(1)
            .after(AmberTypes.FUN_KW).spaces(1)
            .after(AmberTypes.IMPORT_KW).spaces(1)
            .after(AmberTypes.PUB_KW).spaces(1)
            .after(AmberTypes.REF_KW).spaces(1)
            .after(AmberTypes.AWAIT_KW).spaces(1)
            .after(AmberTypes.NOT_KW).spaces(1)
            .around(AmberTypes.AS_KW).spaces(1)
            .around(AmberTypes.IS_KW).spaces(1)
            .around(AmberTypes.IN_KW).spaces(1)
            .around(AmberTypes.THEN_KW).spaces(1)
            .around(AmberTypes.ELSE_KW).spaces(1)
            .around(AmberTypes.FROM_KW).spaces(1)

            // Binary operators (PLUS/MINUS scoped to additive_expression so unary `-x` is unaffected)
            .aroundInside(AmberTypes.PLUS, AmberTypes.ADDITIVE_EXPRESSION).spaces(1)
            .aroundInside(AmberTypes.MINUS, AmberTypes.ADDITIVE_EXPRESSION).spaces(1)
            .around(AmberTypes.STAR).spaces(1)
            .around(AmberTypes.SLASH).spaces(1)
            .around(AmberTypes.PERCENT).spaces(1)
            .around(AmberTypes.AND_KW).spaces(1)
            .around(AmberTypes.OR_KW).spaces(1)
            .around(AmberTypes.EQEQ).spaces(1)
            .around(AmberTypes.NEQ).spaces(1)
            .around(AmberTypes.LE).spaces(1)
            .around(AmberTypes.GE).spaces(1)
            .around(AmberTypes.LT).spaces(1)
            .around(AmberTypes.GT).spaces(1)

            // Assignment and compound assignment
            .around(AmberTypes.EQ).spaces(1)
            .around(AmberTypes.PLUS_EQ).spaces(1)
            .around(AmberTypes.MINUS_EQ).spaces(1)
            .around(AmberTypes.STAR_EQ).spaces(1)
            .around(AmberTypes.SLASH_EQ).spaces(1)
            .around(AmberTypes.PERCENT_EQ).spaces(1)

            // Range operators kept tight: `1..10`, `1..=10`
            .around(AmberTypes.DOTDOT).none()
            .around(AmberTypes.DOTDOTEQ).none()

            // Punctuation
            .after(AmberTypes.COMMA).spaces(1)
            .before(AmberTypes.COMMA).none()
            .after(AmberTypes.COLON).spaces(1)
            .before(AmberTypes.COLON).none()
            .before(AmberTypes.LBRACE).spaces(1)

            // Parens / brackets — no inner padding, no space before opening
            .before(AmberTypes.LPAREN).none()
            .after(AmberTypes.LPAREN).none()
            .before(AmberTypes.RPAREN).none()
            .after(AmberTypes.LBRACK).none()
            .before(AmberTypes.RBRACK).none()
}
