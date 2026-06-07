package org.mvnsearch.jetbrains.amber.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.mvnsearch.jetbrains.amber.AmberLanguage
import org.mvnsearch.jetbrains.amber.AmberStdLibrary
import org.mvnsearch.jetbrains.amber.psi.AmberAttributeName
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath
import org.mvnsearch.jetbrains.amber.psi.AmberTypes

class AttributeNameCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            psiElement(PsiElement::class.java)
                .withElementType(AmberTypes.IDENTIFIER)
                .withParent(AmberAttributeName::class.java)
                .withLanguage(AmberLanguage),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    result.addElement(LookupElementBuilder.create("allow_nested_if_else"))
                    result.addElement(LookupElementBuilder.create("allow_absurd_cast"))
                    result.addElement(LookupElementBuilder.create("allow_camel_case"))
                    result.addElement(LookupElementBuilder.create("allow_dead_code"))
                    result.addElement(LookupElementBuilder.create("allow_public_mutable"))
                }
            }
        )
    }
}
