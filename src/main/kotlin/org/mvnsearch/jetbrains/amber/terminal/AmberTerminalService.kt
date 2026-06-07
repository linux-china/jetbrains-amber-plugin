@file:Suppress("UnstableApiUsage")

package org.mvnsearch.jetbrains.amber.terminal

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalView

@Service(Service.Level.PROJECT)
class AmberTerminalService(val project: Project) {

    fun getOrCreateTerminalView(title: String): ShellTerminalWidget {
        val toolWindow =
            ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)!!
        val targetContent = toolWindow.contentManager.contents.firstOrNull {
            it.displayName == title
        }
        if (targetContent != null) {
            toolWindow.contentManager.setSelectedContent(targetContent, true)
            toolWindow.activate(null)
            return TerminalView.getWidgetByContent(targetContent) as ShellTerminalWidget
        }
        val terminalView = TerminalView.getInstance(project)
        return terminalView.createLocalShellWidget(project.basePath, title, true)
    }
}
