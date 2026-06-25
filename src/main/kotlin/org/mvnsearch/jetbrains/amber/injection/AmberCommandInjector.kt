package org.mvnsearch.jetbrains.amber.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.mvnsearch.jetbrains.amber.psi.AmberCommandLine
import org.mvnsearch.jetbrains.commandline.AmberCommandLanguage

/**
 * Injects the `AmberCommand` language into Amber `$ command_name options $` command lines.
 *
 * The injected range excludes the leading and trailing `$` delimiters so only the command
 * text itself is parsed as `AmberCommand`.
 */
class AmberCommandInjector : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is AmberCommandLine || !context.isValidHost) return
        val text = context.text
        // A command line is `$ ... $`; we need at least the two delimiters.
        if (text.length < 2 || !text.startsWith("$") || !text.endsWith("$")) return
        // Range between the two `$` delimiters, never including them.
        val innerRange = TextRange(1, text.length - 1)
        if (innerRange.isEmpty) return
        registrar.startInjecting(AmberCommandLanguage)
            .addPlace(null, null, context, innerRange)
            .doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> = listOf(AmberCommandLine::class.java)
}
