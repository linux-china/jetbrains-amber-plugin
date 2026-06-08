package org.mvnsearch.jetbrains.amber.index

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import org.mvnsearch.jetbrains.amber.AmberFileType
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitConst
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitMut
import org.mvnsearch.jetbrains.amber.psi.AmberVisibility
import java.io.DataInput
import java.io.DataOutput

enum class AmberSymbolKind { FUNCTION, VARIABLE }

/**
 * File-based index of every top-level `pub fun` / `pub let` / `pub const`
 * declaration across the project, keyed by the declaration name and tagged
 * with [AmberSymbolKind] so the consumer can filter functions vs variables
 * without re-parsing.
 *
 * Built incrementally: when a `.ab` file changes, only that file's entries
 * are reindexed; queries are O(matching files), independent of project size.
 *
 * Use [AmberPubSymbols] for the lookup API.
 */
class AmberPubSymbolIndex : FileBasedIndexExtension<String, AmberSymbolKind>() {

    override fun getName(): ID<String, AmberSymbolKind> = INDEX_ID

    override fun getIndexer(): DataIndexer<String, AmberSymbolKind, FileContent> =
        DataIndexer { fileContent ->
            val map = mutableMapOf<String, AmberSymbolKind>()
            val file = fileContent.psiFile as? AmberFile ?: return@DataIndexer map
            for (child in file.children) {
                if (!isPublic(child)) continue
                when (child) {
                    is AmberFunctionDef -> child.name?.let { map[it] = AmberSymbolKind.FUNCTION }
                    is AmberVariableInitMut -> child.name?.let { map[it] = AmberSymbolKind.VARIABLE }
                    is AmberVariableInitConst -> child.name?.let { map[it] = AmberSymbolKind.VARIABLE }
                }
            }
            map
        }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<AmberSymbolKind> = AmberSymbolKindExternalizer

    override fun getVersion(): Int = 1

    override fun dependsOnFileContent(): Boolean = true

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        DefaultFileTypeSpecificInputFilter(AmberFileType)

    private fun isPublic(element: PsiElement): Boolean =
        PsiTreeUtil.getChildOfType(element, AmberVisibility::class.java) != null

    companion object {
        val INDEX_ID: ID<String, AmberSymbolKind> = ID.create("amber.pub.symbols")
    }
}

private object AmberSymbolKindExternalizer : DataExternalizer<AmberSymbolKind> {
    override fun save(out: DataOutput, value: AmberSymbolKind) {
        out.writeByte(value.ordinal)
    }

    override fun read(input: DataInput): AmberSymbolKind =
        AmberSymbolKind.entries[input.readUnsignedByte()]
}
