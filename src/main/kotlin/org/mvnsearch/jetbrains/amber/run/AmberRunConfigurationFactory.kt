package org.mvnsearch.jetbrains.amber.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class AmberRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {

    override fun getId(): String = AmberRunConfigurationType.ID

    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        AmberRunConfiguration(project, this, "Amber")
}
