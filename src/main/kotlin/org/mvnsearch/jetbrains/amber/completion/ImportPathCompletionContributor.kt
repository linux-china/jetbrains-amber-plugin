package org.mvnsearch.jetbrains.amber.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.mvnsearch.jetbrains.amber.AmberLanguage
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class ImportPathCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            psiElement(PsiElement::class.java)
                .withElementType(AmberTypes.STRING)
                .withParent(AmberImportPath::class.java)
                .withLanguage(AmberLanguage),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val element = parameters.position
                    val text = element.text.trim('"')
                    if (text.startsWith("./")) {
                        element.containingFile?.originalFile?.virtualFile?.parent?.let { dir ->
                            dir.children.filter { it.name.endsWith(".ab") }.forEach {
                                result.addElement(LookupElementBuilder.create(it.name))
                            }
                        }
                    } else if (!text.startsWith(".")) {
                        result.addElement(LookupElementBuilder.create("std/http"))
                        result.addElement(LookupElementBuilder.create("std/array"))
                    }
                }
            }
        )
    }
}
