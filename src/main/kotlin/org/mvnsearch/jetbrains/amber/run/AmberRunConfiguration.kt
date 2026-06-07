package org.mvnsearch.jetbrains.amber.run

import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element

class AmberRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : LocatableConfigurationBase<RunConfigurationOptions>(project, factory, name) {

    var scriptPath: String = ""
    var scriptArgs: String = ""
    var workingDirectory: String = ""
    var envData: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT
    var isTest: Boolean = false
    var testCase: String = ""

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        AmberRunConfigurationEditor(project)

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
        AmberRunProfileState(environment, this)

    override fun checkConfiguration() {
        if (scriptPath.isBlank()) {
            throw RuntimeConfigurationError("Script path must be specified")
        }
        if (isTest && testCase.isNotBlank() && testCase.contains('"')) {
            throw RuntimeConfigurationError("Test case name cannot contain a double quote")
        }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JDOMExternalizerUtil.writeField(element, "SCRIPT_PATH", scriptPath)
        JDOMExternalizerUtil.writeField(element, "SCRIPT_ARGS", scriptArgs)
        JDOMExternalizerUtil.writeField(element, "WORKING_DIRECTORY", workingDirectory)
        JDOMExternalizerUtil.writeField(element, "IS_TEST", isTest.toString())
        JDOMExternalizerUtil.writeField(element, "TEST_CASE", testCase)
        envData.writeExternal(element)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        scriptPath = JDOMExternalizerUtil.readField(element, "SCRIPT_PATH") ?: ""
        scriptArgs = JDOMExternalizerUtil.readField(element, "SCRIPT_ARGS") ?: ""
        workingDirectory = JDOMExternalizerUtil.readField(element, "WORKING_DIRECTORY") ?: ""
        isTest = JDOMExternalizerUtil.readField(element, "IS_TEST")?.toBoolean() ?: false
        testCase = JDOMExternalizerUtil.readField(element, "TEST_CASE") ?: ""
        envData = EnvironmentVariablesData.readExternal(element)
    }
}
