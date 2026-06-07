package org.mvnsearch.jetbrains.amber.run

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.util.execution.ParametersListUtil

class AmberRunProfileState(
    environment: ExecutionEnvironment,
    private val config: AmberRunConfiguration
) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        val workDir = config.workingDirectory.ifBlank {
            environment.project.basePath ?: System.getProperty("user.dir")
        }

        val parameters = mutableListOf<String>()
        if (config.isTest) parameters.add("test")
        parameters.add(config.scriptPath)
        if (config.isTest && config.testCase.isNotBlank()) {
            parameters.add("--test-case")
            parameters.add(config.testCase)
        }
        parameters.addAll(ParametersListUtil.parse(config.scriptArgs))

        val commandLine = GeneralCommandLine()
            .withExePath("amber")
            .withWorkDirectory(workDir)
            .withParameters(parameters)
            .withEnvironment(config.envData.envs)
            .withParentEnvironmentType(
                if (config.envData.isPassParentEnvs)
                    GeneralCommandLine.ParentEnvironmentType.CONSOLE
                else
                    GeneralCommandLine.ParentEnvironmentType.NONE
            )

        val handler = KillableColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(handler)
        return handler
    }
}
