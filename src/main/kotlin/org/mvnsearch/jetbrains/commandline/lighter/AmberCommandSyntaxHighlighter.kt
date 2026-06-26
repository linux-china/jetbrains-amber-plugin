package org.mvnsearch.jetbrains.commandline.lighter

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.mvnsearch.jetbrains.commandline.lexer.AmberCommandLexer
import org.mvnsearch.jetbrains.commandline.psi.AmberCommandTypes

class AmberCommandSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = FlexAdapter(AmberCommandLexer(null))

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        in OPERATORS -> OPERATOR_KEYS
        in BRACES -> BRACE_KEYS
        AmberCommandTypes.VARIABLE -> VARIABLE_KEYS
        AmberCommandTypes.GLOB -> GLOB_KEYS
        AmberCommandTypes.DQ_STRING -> STRING_KEYS
        AmberCommandTypes.SQ_STRING -> STRING_KEYS
        AmberCommandTypes.COMMENT -> COMMENT_KEYS
        TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
        else -> emptyArray()
    }

    companion object {
        val OPERATOR: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val BRACE: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND_BRACE", DefaultLanguageHighlighterColors.BRACES)
        val VARIABLE: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND_VARIABLE", DefaultLanguageHighlighterColors.STATIC_FIELD)
        val GLOB: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND_GLOB", DefaultLanguageHighlighterColors.METADATA)
        val STRING: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND_STRING", DefaultLanguageHighlighterColors.STRING)
        val COMMENT: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BAD_CHARACTER: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val OPERATOR_KEYS = arrayOf(OPERATOR)
        private val BRACE_KEYS = arrayOf(BRACE)
        private val VARIABLE_KEYS = arrayOf(VARIABLE)
        private val GLOB_KEYS = arrayOf(GLOB)
        private val STRING_KEYS = arrayOf(STRING)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)

        private val OPERATORS: Set<IElementType> = setOf(
            AmberCommandTypes.PIPE, AmberCommandTypes.GT, AmberCommandTypes.LT,
            AmberCommandTypes.GT_GT, AmberCommandTypes.AMP, AmberCommandTypes.AMP_AMP,
            AmberCommandTypes.PIPE_PIPE, AmberCommandTypes.SEMICOLON,
            AmberCommandTypes.DASH, AmberCommandTypes.DASH_DASH,
            AmberCommandTypes.EQ, AmberCommandTypes.COLON
        )

        private val BRACES: Set<IElementType> = setOf(
            AmberCommandTypes.LBRACE, AmberCommandTypes.RBRACE
        )
    }
}
