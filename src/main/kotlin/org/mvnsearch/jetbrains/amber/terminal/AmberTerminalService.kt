@file:Suppress("UnstableApiUsage")

package org.mvnsearch.jetbrains.amber.terminal

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.terminal.frontend.toolwindow.TerminalToolWindowTabsManager
import com.intellij.terminal.frontend.view.TerminalView

@Service(Service.Level.PROJECT)
class AmberTerminalService(val project: Project) {

    fun getOrCreateTerminalView(title: String): TerminalView {
        val tabsManager = TerminalToolWindowTabsManager.getInstance(project)
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal")!!
        toolWindow.contentManager.findContent(title)?.let {
            if (it.component is TerminalView) {
                toolWindow.contentManager.setSelectedContent(it, true)
                toolWindow.activate(null)
                return it.component as TerminalView
            }
        }
        return tabsManager.createTabBuilder()
            .tabName(title)
            .createTab()
            .view
    }
}
