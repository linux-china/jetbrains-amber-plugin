package org.mvnsearch.jetbrains.amber.formatter

import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.ChildAttributes
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AmberBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    private val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<Block> {
        val children = mutableListOf<Block>()
        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE && child.textRange.length > 0) {
                children.add(AmberBlock(child, null, null, spacingBuilder))
            }
            child = child.treeNext
        }
        return children
    }

    override fun getIndent(): Indent? {
        val parent = myNode.treeParent ?: return Indent.getNoneIndent()
        val type = myNode.elementType
        if (parent.elementType == AmberTypes.MULTILINE_BLOCK) {
            return if (type == AmberTypes.LBRACE || type == AmberTypes.RBRACE) {
                Indent.getNoneIndent()
            } else {
                Indent.getNormalIndent()
            }
        }
        return Indent.getNoneIndent()
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? =
        spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf(): Boolean = myNode.firstChildNode == null

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        val indent = if (myNode.elementType == AmberTypes.MULTILINE_BLOCK) {
            Indent.getNormalIndent()
        } else {
            Indent.getNoneIndent()
        }
        return ChildAttributes(indent, null)
    }
}
