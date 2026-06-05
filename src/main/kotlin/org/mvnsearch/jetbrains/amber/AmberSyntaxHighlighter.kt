package org.mvnsearch.jetbrains.amber

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.mvnsearch.jetbrains.amber.lexer.AmberLexer
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = FlexAdapter(AmberLexer(null))

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        in KEYWORDS -> KEYWORD_KEYS
        in TYPE_NAMES -> TYPE_KEYS
        in LITERAL_KEYWORDS -> LITERAL_KEYS
        in OPERATORS -> OPERATOR_KEYS
        in PARENTHESES -> PAREN_KEYS
        in BRACKETS -> BRACKET_KEYS
        in BRACES -> BRACE_KEYS
        AmberTypes.COMMA -> COMMA_KEYS
        AmberTypes.COLON -> COLON_KEYS
        AmberTypes.NUMBER -> NUMBER_KEYS
        AmberTypes.STRING -> STRING_KEYS
        AmberTypes.COMMAND -> COMMAND_KEYS
        AmberTypes.IDENTIFIER -> IDENTIFIER_KEYS
        AmberTypes.SHEBANG -> SHEBANG_KEYS
        AmberTypes.LINE_COMMENT -> LINE_COMMENT_KEYS
        AmberTypes.DOC_COMMENT -> DOC_COMMENT_KEYS
        TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
        else -> emptyArray()
    }

    companion object {
        val KEYWORD: TextAttributesKey =
            createTextAttributesKey("AMBER_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val TYPE_NAME: TextAttributesKey =
            createTextAttributesKey("AMBER_TYPE_NAME", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
        val LITERAL_KEYWORD: TextAttributesKey =
            createTextAttributesKey("AMBER_LITERAL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val NUMBER: TextAttributesKey =
            createTextAttributesKey("AMBER_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val STRING: TextAttributesKey =
            createTextAttributesKey("AMBER_STRING", DefaultLanguageHighlighterColors.STRING)
        val COMMAND: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMAND", DefaultLanguageHighlighterColors.CONSTANT)
        val IDENTIFIER: TextAttributesKey =
            createTextAttributesKey("AMBER_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val FUNCTION_DECLARATION: TextAttributesKey =
            createTextAttributesKey("AMBER_FUNCTION_DECLARATION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val FUNCTION_CALL: TextAttributesKey =
            createTextAttributesKey("AMBER_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL)
        val PARAMETER: TextAttributesKey =
            createTextAttributesKey("AMBER_PARAMETER", DefaultLanguageHighlighterColors.PARAMETER)
        val OPERATOR: TextAttributesKey =
            createTextAttributesKey("AMBER_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val PAREN: TextAttributesKey =
            createTextAttributesKey("AMBER_PAREN", DefaultLanguageHighlighterColors.PARENTHESES)
        val BRACKET: TextAttributesKey =
            createTextAttributesKey("AMBER_BRACKET", DefaultLanguageHighlighterColors.BRACKETS)
        val BRACE: TextAttributesKey =
            createTextAttributesKey("AMBER_BRACE", DefaultLanguageHighlighterColors.BRACES)
        val COMMA: TextAttributesKey =
            createTextAttributesKey("AMBER_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val COLON: TextAttributesKey =
            createTextAttributesKey("AMBER_COLON", DefaultLanguageHighlighterColors.SEMICOLON)
        val SHEBANG: TextAttributesKey =
            createTextAttributesKey("AMBER_SHEBANG", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val LINE_COMMENT: TextAttributesKey =
            createTextAttributesKey("AMBER_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val DOC_COMMENT: TextAttributesKey =
            createTextAttributesKey("AMBER_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT)
        val BAD_CHARACTER: TextAttributesKey =
            createTextAttributesKey("AMBER_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val TYPE_KEYS = arrayOf(TYPE_NAME)
        private val LITERAL_KEYS = arrayOf(LITERAL_KEYWORD)
        private val OPERATOR_KEYS = arrayOf(OPERATOR)
        private val PAREN_KEYS = arrayOf(PAREN)
        private val BRACKET_KEYS = arrayOf(BRACKET)
        private val BRACE_KEYS = arrayOf(BRACE)
        private val COMMA_KEYS = arrayOf(COMMA)
        private val COLON_KEYS = arrayOf(COLON)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val STRING_KEYS = arrayOf(STRING)
        private val COMMAND_KEYS = arrayOf(COMMAND)
        private val IDENTIFIER_KEYS = arrayOf(IDENTIFIER)
        private val SHEBANG_KEYS = arrayOf(SHEBANG)
        private val LINE_COMMENT_KEYS = arrayOf(LINE_COMMENT)
        private val DOC_COMMENT_KEYS = arrayOf(DOC_COMMENT)
        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)

        private val KEYWORDS: Set<IElementType> = setOf(
            AmberTypes.AND_KW, AmberTypes.AS_KW, AmberTypes.AWAIT_KW, AmberTypes.BREAK_KW,
            AmberTypes.CD_KW, AmberTypes.CLEAR_KW, AmberTypes.CONST_KW, AmberTypes.CONTINUE_KW,
            AmberTypes.CP_KW, AmberTypes.DISOWN_KW, AmberTypes.ECHO_KW, AmberTypes.ELSE_KW,
            AmberTypes.EXIT_KW, AmberTypes.EXITED_KW, AmberTypes.FAIL_KW, AmberTypes.FAILED_KW,
            AmberTypes.FOR_KW, AmberTypes.FROM_KW, AmberTypes.FUN_KW, AmberTypes.IF_KW,
            AmberTypes.IMPORT_KW, AmberTypes.IN_KW, AmberTypes.IS_KW, AmberTypes.LEN_KW,
            AmberTypes.LET_KW, AmberTypes.LINES_KW, AmberTypes.LOCK_KW, AmberTypes.LOOP_KW,
            AmberTypes.LS_KW, AmberTypes.MAIN_KW, AmberTypes.MV_KW, AmberTypes.NAMEOF_KW,
            AmberTypes.NOT_KW, AmberTypes.OR_KW, AmberTypes.PID_KW, AmberTypes.PUB_KW,
            AmberTypes.PWD_KW, AmberTypes.REF_KW, AmberTypes.RETURN_KW, AmberTypes.RM_KW,
            AmberTypes.SHELLNAME_KW, AmberTypes.SHELLVERSION_KW, AmberTypes.SILENT_KW,
            AmberTypes.SLEEP_KW, AmberTypes.STATUS_KW, AmberTypes.SUCCEEDED_KW, AmberTypes.SUDO_KW,
            AmberTypes.SUPPRESS_KW, AmberTypes.TEST_KW, AmberTypes.THEN_KW, AmberTypes.TOUCH_KW,
            AmberTypes.TRUST_KW, AmberTypes.UNSAFE_KW, AmberTypes.WHILE_KW
        )

        private val TYPE_NAMES: Set<IElementType> = setOf(
            AmberTypes.TYPE_TEXT, AmberTypes.TYPE_NUM, AmberTypes.TYPE_BOOL,
            AmberTypes.TYPE_NULL, AmberTypes.TYPE_INT
        )

        private val LITERAL_KEYWORDS: Set<IElementType> = setOf(
            AmberTypes.TRUE, AmberTypes.FALSE, AmberTypes.NULL
        )

        private val OPERATORS: Set<IElementType> = setOf(
            AmberTypes.PLUS, AmberTypes.MINUS, AmberTypes.STAR, AmberTypes.SLASH, AmberTypes.PERCENT,
            AmberTypes.EQ, AmberTypes.EQEQ, AmberTypes.NEQ,
            AmberTypes.LT, AmberTypes.LE, AmberTypes.GT, AmberTypes.GE,
            AmberTypes.PLUS_EQ, AmberTypes.MINUS_EQ, AmberTypes.STAR_EQ,
            AmberTypes.SLASH_EQ, AmberTypes.PERCENT_EQ,
            AmberTypes.QUESTION, AmberTypes.DOTDOT, AmberTypes.DOTDOTEQ, AmberTypes.PIPE
        )

        private val PARENTHESES: Set<IElementType> = setOf(AmberTypes.LPAREN, AmberTypes.RPAREN)
        private val BRACKETS: Set<IElementType> = setOf(
            AmberTypes.LBRACK, AmberTypes.RBRACK, AmberTypes.HASH_LBRACK
        )
        private val BRACES: Set<IElementType> = setOf(AmberTypes.LBRACE, AmberTypes.RBRACE)
    }
}
