package org.mvnsearch.jetbrains.amber.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeUtil
import org.mvnsearch.jetbrains.amber.AmberIcons
import javax.swing.Icon

class AmberRunConfigurationType : ConfigurationType {

    private val factory = AmberRunConfigurationFactory(this)

    override fun getDisplayName(): String = "Amber"
    override fun getConfigurationTypeDescription(): String = "Run Amber scripts and tests"
    override fun getIcon(): Icon = AmberIcons.FILE
    override fun getId(): String = ID
    override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(factory)

    companion object {
        const val ID: String = "AmberRunConfiguration"

        fun getInstance(): AmberRunConfigurationType =
            ConfigurationTypeUtil.findConfigurationType(AmberRunConfigurationType::class.java)
    }
}
