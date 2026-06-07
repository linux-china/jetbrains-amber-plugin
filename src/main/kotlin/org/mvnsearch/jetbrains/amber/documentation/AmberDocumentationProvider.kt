package org.mvnsearch.jetbrains.amber.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import org.mvnsearch.jetbrains.amber.psi.AmberDocCommentStmt
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberImportId
import org.mvnsearch.jetbrains.amber.psi.AmberMainDef
import org.mvnsearch.jetbrains.amber.psi.AmberParameter
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitConst
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitDestruct
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitMut

class AmberDocumentationProvider : AbstractDocumentationProvider() {

    override fun getQuickNavigateInfo(element: PsiElement, originalElement: PsiElement?): String? {
        val target = findDeclaration(element) ?: return null
        return buildSignature(target)
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val target = findDeclaration(element) ?: return null
        val signature = buildSignature(target)
        val doc = collectLeadingDocComment(target)
        return buildHtml(signature, doc)
    }

    private fun findDeclaration(element: PsiElement): PsiElement? {
        var current: PsiElement? = element
        while (current != null && current !is PsiFile) {
            when (current) {
                is AmberFunctionDef-> return current
            }
            current = current.parent
        }
        return null
    }

    private fun buildSignature(target: PsiElement): String = when (target) {
        is AmberFunctionDef -> textBefore(target, target.block)
        is AmberMainDef -> textBefore(target, target.block)
        is AmberImportId -> "import ${target.text.trim()}"
        else -> target.text.trim()
    }

    private fun textBefore(owner: PsiElement, child: PsiElement): String {
        val offset = child.startOffsetInParent
        return owner.text.substring(0, offset).trim()
    }

    private fun collectLeadingDocComment(target: PsiElement): String? {
        val lines = mutableListOf<String>()
        var sibling = target.prevSibling
        while (sibling != null) {
            when (sibling) {
                is PsiWhiteSpace -> { /* skip */ }
                is AmberDocCommentStmt -> {
                    val text = sibling.text.removePrefix("///").trimStart()
                    lines.add(0, text.trimEnd())
                }
                else -> break
            }
            sibling = sibling.prevSibling
        }
        return if (lines.isEmpty()) null else lines.joinToString("\n")
    }

    private fun buildHtml(signature: String, doc: String?): String {
        val sb = StringBuilder()
        sb.append(DocumentationMarkup.DEFINITION_START)
        sb.append("<pre>").append(StringUtil.escapeXmlEntities(signature)).append("</pre>")
        sb.append(DocumentationMarkup.DEFINITION_END)
        if (!doc.isNullOrEmpty()) {
            sb.append(DocumentationMarkup.CONTENT_START)
            sb.append(StringUtil.escapeXmlEntities(doc).replace("\n", "<br/>"))
            sb.append(DocumentationMarkup.CONTENT_END)
        }
        return sb.toString()
    }
}
