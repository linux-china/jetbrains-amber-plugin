package org.mvnsearch.jetbrains.amber.template

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider
import com.intellij.codeInsight.template.postfix.templates.StringBasedPostfixTemplate
import com.intellij.psi.PsiElement

class AmberIfPostfixTemplate(provider: PostfixTemplateProvider) : StringBasedPostfixTemplate(
    "if",
    "if expr { ... }",
    AmberPostfixExpressionSelector(),
    provider
) {
    override fun getTemplateString(element: PsiElement): String =
        "if ${element.text} {\n    \$END\$\n}"
}

class AmberWhilePostfixTemplate(provider: PostfixTemplateProvider) : StringBasedPostfixTemplate(
    "while",
    "while expr { ... }",
    AmberPostfixExpressionSelector(),
    provider
) {
    override fun getTemplateString(element: PsiElement): String =
        "while ${element.text} {\n    \$END\$\n}"
}

class AmberLetPostfixTemplate(provider: PostfixTemplateProvider) : StringBasedPostfixTemplate(
    "let",
    "let name = expr",
    AmberPostfixExpressionSelector(),
    provider
) {
    override fun getTemplateString(element: PsiElement): String =
        "let \$END\$ = ${element.text}"
}

class AmberConstPostfixTemplate(provider: PostfixTemplateProvider) : StringBasedPostfixTemplate(
    "const",
    "const name = expr",
    AmberPostfixExpressionSelector(),
    provider
) {
    override fun getTemplateString(element: PsiElement): String =
        "const \$NAME\$ = ${element.text}\$END\$"
}

class AmberEchoPostfixTemplate(provider: PostfixTemplateProvider) : StringBasedPostfixTemplate(
    "echo",
    "echo(expr)",
    AmberPostfixExpressionSelector(),
    provider
) {
    override fun getTemplateString(element: PsiElement): String =
        "echo(${element.text})\$END\$"
}


