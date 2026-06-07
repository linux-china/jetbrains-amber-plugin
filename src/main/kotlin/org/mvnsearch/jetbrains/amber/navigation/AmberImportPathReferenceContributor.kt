package org.mvnsearch.jetbrains.amber.navigation

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath

class AmberImportPathReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(AmberImportPath::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<PsiReference> {
                    val importPath = element as AmberImportPath
                    val text = importPath.text
                    if (text.length < 2 || !text.startsWith("\"") || !text.endsWith("\"")) {
                        return PsiReference.EMPTY_ARRAY
                    }
                    val inner = text.substring(1, text.length - 1)
                    if (inner.isEmpty()) return PsiReference.EMPTY_ARRAY
                    return arrayOf(AmberImportPathReference(importPath, TextRange(1, text.length - 1)))
                }
            }
        )
    }
}
