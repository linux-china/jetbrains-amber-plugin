package org.mvnsearch.jetbrains.amber.index

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberNamedElement
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitConst
import org.mvnsearch.jetbrains.amber.psi.AmberVariableInitMut

/**
 * Query API on top of [AmberPubSymbolIndex] for cross-file `pub` lookups.
 *
 *  - [findFunctions] / [findVariables] resolve a name to all matching `pub`
 *    declarations across the project. The index returns the carrying file;
 *    the actual PSI is obtained via [PsiManager.findFile] and the declaration
 *    is fetched from [AmberFile.findNamedElement].
 *  - [allFunctionNames] / [allVariableNames] enumerate every `pub` name —
 *    useful for `ChooseByNameContributor` (Goto Symbol) and auto-import-style
 *    completions over project sources.
 */
object AmberPubSymbols {

    fun findFunctions(
        project: Project,
        name: String,
        scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
    ): List<AmberFunctionDef> =
        findDeclarations(project, name, scope, AmberSymbolKind.FUNCTION)
            .filterIsInstance<AmberFunctionDef>()

    fun findVariables(
        project: Project,
        name: String,
        scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
    ): List<AmberNamedElement> =
        findDeclarations(project, name, scope, AmberSymbolKind.VARIABLE)
            .filter { it is AmberVariableInitMut || it is AmberVariableInitConst }

    fun allFunctionNames(
        project: Project,
        scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
    ): Set<String> = allNames(scope, AmberSymbolKind.FUNCTION)

    fun allVariableNames(
        project: Project,
        scope: GlobalSearchScope = GlobalSearchScope.allScope(project)
    ): Set<String> = allNames(scope, AmberSymbolKind.VARIABLE)

    private fun findDeclarations(
        project: Project,
        name: String,
        scope: GlobalSearchScope,
        kind: AmberSymbolKind
    ): List<AmberNamedElement> {
        val results = mutableListOf<AmberNamedElement>()
        val psiManager = PsiManager.getInstance(project)
        FileBasedIndex.getInstance().processValues(
            AmberPubSymbolIndex.INDEX_ID,
            name,
            null,
            { virtualFile, indexedKind ->
                if (indexedKind == kind) {
                    val file = psiManager.findFile(virtualFile) as? AmberFile
                    file?.findNamedElement(name)?.let { results.add(it) }
                }
                true
            },
            scope
        )
        return results
    }

    private fun allNames(scope: GlobalSearchScope, kind: AmberSymbolKind): Set<String> {
        val results = mutableSetOf<String>()
        val index = FileBasedIndex.getInstance()
        index.processAllKeys(
            AmberPubSymbolIndex.INDEX_ID,
            { name ->
                var matched = false
                index.processValues(
                    AmberPubSymbolIndex.INDEX_ID,
                    name,
                    null,
                    { _, indexedKind ->
                        if (indexedKind == kind) {
                            matched = true
                            false   // short-circuit on first match
                        } else {
                            true
                        }
                    },
                    scope
                )
                if (matched) results.add(name)
                true
            },
            scope,
            null
        )
        return results
    }
}
