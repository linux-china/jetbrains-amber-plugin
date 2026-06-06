package org.mvnsearch.jetbrains.amber.fold

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.mvnsearch.jetbrains.amber.psi.AmberCommandCall
import org.mvnsearch.jetbrains.amber.psi.AmberMultilineBlock
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberFoldingBuilder : FoldingBuilderEx(), DumbAware {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()

        PsiTreeUtil.findChildrenOfType(root, AmberMultilineBlock::class.java).forEach { block ->
            addIfMultiLine(descriptors, block.node, block.textRange, document)
        }

        PsiTreeUtil.findChildrenOfType(root, AmberCommandCall::class.java).forEach { call ->
            val cmd = call.node.findChildByType(AmberTypes.COMMAND) ?: return@forEach
            addIfMultiLine(descriptors, cmd, cmd.textRange, document)
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String = when (node.elementType) {
        AmberTypes.MULTILINE_BLOCK -> "{...}"
        AmberTypes.COMMAND -> "$...$"
        else -> "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    private fun addIfMultiLine(
        out: MutableList<FoldingDescriptor>,
        node: ASTNode,
        range: TextRange,
        document: Document
    ) {
        if (range.endOffset > document.textLength) return
        val startLine = document.getLineNumber(range.startOffset)
        val endLine = document.getLineNumber(range.endOffset)
        if (endLine > startLine) {
            out.add(FoldingDescriptor(node, range))
        }
    }
}