package org.mvnsearch.jetbrains.amber.template

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class AmberPostfixTemplateProvider : PostfixTemplateProvider {

    private val templates: Set<PostfixTemplate> = setOf(
        AmberIfPostfixTemplate(this),
        AmberWhilePostfixTemplate(this),
        AmberLetPostfixTemplate(this),
        AmberConstPostfixTemplate(this),
        AmberEchoPostfixTemplate(this),
    )

    override fun getTemplates(): Set<PostfixTemplate> = templates

    override fun isTerminalSymbol(currentChar: Char): Boolean =
        currentChar == '.' || currentChar == '!'

    override fun preExpand(file: PsiFile, editor: Editor) {}

    override fun afterExpand(file: PsiFile, editor: Editor) {}

    override fun preCheck(copyFile: PsiFile, realEditor: Editor, currentOffset: Int): PsiFile = copyFile
}
