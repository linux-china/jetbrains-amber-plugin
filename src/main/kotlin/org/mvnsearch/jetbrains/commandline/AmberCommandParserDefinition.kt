package org.mvnsearch.jetbrains.commandline

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.mvnsearch.jetbrains.commandline.lexer.AmberCommandLexer
import org.mvnsearch.jetbrains.commandline.parser.AmberCommandParser
import org.mvnsearch.jetbrains.commandline.psi.AmberCommandFile
import org.mvnsearch.jetbrains.commandline.psi.AmberCommandTypes

class AmberCommandParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer = FlexAdapter(AmberCommandLexer(null))

    override fun createParser(project: Project?): PsiParser = AmberCommandParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = STRINGS

    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES

    override fun createElement(node: ASTNode): PsiElement = AmberCommandTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = AmberCommandFile(viewProvider)

    companion object {
        val FILE: IFileElementType = IFileElementType(AmberCommandLanguage)
        val WHITE_SPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE, AmberCommandTypes.NEWLINE)
        val COMMENTS: TokenSet = TokenSet.create(AmberCommandTypes.COMMENT)
        val STRINGS: TokenSet = TokenSet.create(AmberCommandTypes.DQ_STRING, AmberCommandTypes.SQ_STRING)
    }
}
