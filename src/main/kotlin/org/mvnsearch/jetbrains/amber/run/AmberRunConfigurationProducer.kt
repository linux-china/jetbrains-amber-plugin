package org.mvnsearch.jetbrains.amber.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberTestDef

class AmberRunConfigurationProducer : LazyRunConfigurationProducer<AmberRunConfiguration>() {

    override fun getConfigurationFactory(): ConfigurationFactory =
        AmberRunConfigurationType.getInstance().configurationFactories[0]

    override fun setupConfigurationFromContext(
        configuration: AmberRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val location = context.psiLocation ?: return false
        val file = location.containingFile as? AmberFile ?: return false
        val virtualFile = file.virtualFile ?: return false

        val relativePath = relativePath(context, virtualFile) ?: virtualFile.path
        val testDef = PsiTreeUtil.getParentOfType(location, AmberTestDef::class.java, false)

        configuration.scriptPath = relativePath
        configuration.workingDirectory = context.project.basePath ?: ""

        if (testDef != null) {
            configuration.isTest = true
            val name = testDef.testName?.text?.trim('"').orEmpty()
            configuration.testCase = name
            configuration.name = if (name.isNotEmpty()) "${file.name}: $name" else "${file.name} (tests)"
            sourceElement.set(testDef)
        } else {
            configuration.isTest = false
            configuration.testCase = ""
            configuration.name = file.name
            sourceElement.set(file)
        }
        return true
    }

    override fun isConfigurationFromContext(
        configuration: AmberRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val location = context.psiLocation ?: return false
        val file = location.containingFile as? AmberFile ?: return false
        val virtualFile = file.virtualFile ?: return false
        val relativePath = relativePath(context, virtualFile) ?: virtualFile.path
        if (configuration.scriptPath != relativePath) return false

        val testDef = PsiTreeUtil.getParentOfType(location, AmberTestDef::class.java, false)
        return if (testDef != null) {
            val name = testDef.testName?.text?.trim('"').orEmpty()
            configuration.isTest && configuration.testCase == name
        } else {
            !configuration.isTest
        }
    }

    private fun relativePath(context: ConfigurationContext, file: VirtualFile): String? {
        val base = context.project.guessProjectDir() ?: return null
        return VfsUtilCore.getRelativePath(file, base)
    }
}
