package org.mvnsearch.jetbrains.amber.run

import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class AmberRunConfigurationEditor(private val project: Project) : SettingsEditor<AmberRunConfiguration>() {

    private val scriptPathField = TextFieldWithBrowseButton()
    private val scriptArgsField = JBTextField()
    private val workingDirField = TextFieldWithBrowseButton()
    private val envVarsComponent = EnvironmentVariablesComponent()
    private val testCheckbox = JBCheckBox("Run as test (use `amber test`)")
    private val testCaseField = JBTextField()

    init {
        scriptPathField.addBrowseFolderListener(
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor("ab")
                .withTitle("Select Amber Script")
                .withDescription("Choose the .ab file to run")
        )
        workingDirField.addBrowseFolderListener(
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Select Working Directory")
                .withDescription("Choose the working directory")
        )
    }

    override fun createEditor(): JComponent =
        FormBuilder.createFormBuilder()
            .addLabeledComponent("Script:", scriptPathField)
            .addLabeledComponent("Arguments:", scriptArgsField)
            .addLabeledComponent("Working directory:", workingDirField)
            .addLabeledComponent(envVarsComponent.label, envVarsComponent)
            .addComponent(testCheckbox)
            .addLabeledComponent("Test case:", testCaseField)
            .panel

    override fun resetEditorFrom(s: AmberRunConfiguration) {
        scriptPathField.text = s.scriptPath
        scriptArgsField.text = s.scriptArgs
        workingDirField.text = s.workingDirectory
        envVarsComponent.envData = s.envData
        testCheckbox.isSelected = s.isTest
        testCaseField.text = s.testCase
    }

    override fun applyEditorTo(s: AmberRunConfiguration) {
        s.scriptPath = scriptPathField.text
        s.scriptArgs = scriptArgsField.text
        s.workingDirectory = workingDirField.text
        s.envData = envVarsComponent.envData
        s.isTest = testCheckbox.isSelected
        s.testCase = testCaseField.text
    }
}
