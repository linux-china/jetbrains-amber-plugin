package org.mvnsearch.jetbrains.amber

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
import org.mvnsearch.jetbrains.amber.lexer.AmberLexer
import org.mvnsearch.jetbrains.amber.parser.AmberParser
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer = FlexAdapter(AmberLexer(null))

    override fun createParser(project: Project?): PsiParser = AmberParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = STRINGS

    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES

    override fun createElement(node: ASTNode): PsiElement = AmberTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = AmberFile(viewProvider)

    companion object {
        val FILE: IFileElementType = IFileElementType(AmberLanguage)
        val WHITE_SPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)
        val COMMENTS: TokenSet = TokenSet.create(AmberTypes.LINE_COMMENT, AmberTypes.DOC_COMMENT)
        val STRINGS: TokenSet = TokenSet.create(AmberTypes.STRING)
    }
}
