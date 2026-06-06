@file:Suppress("UnstableApiUsage")

package org.mvnsearch.jetbrains.amber.terminal

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.terminal.frontend.toolwindow.TerminalToolWindowTabsManager
import com.intellij.terminal.frontend.view.TerminalView
import org.jetbrains.plugins.terminal.TerminalToolWindowPanel

@Service(Service.Level.PROJECT)
class AmberTerminalService(val project: Project) {

    fun getOrCreateTerminalView(title: String): TerminalView {
        val tabsManager = TerminalToolWindowTabsManager.getInstance(project)
        val targetTab = tabsManager.tabs.find { it.content.tabName == title }
        if (targetTab != null) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal")!!
            toolWindow.contentManager.setSelectedContent(targetTab.content, true)
            toolWindow.activate(null)
            return targetTab.view
        }
//        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal")!!
//        toolWindow.contentManager.findContent(title)?.let {
//            if(it.component is TerminalToolWindowPanel) {
//                toolWindow.contentManager.setSelectedContent(it, true)
//                toolWindow.activate(null)
//                val terminalPanel = it.component as TerminalToolWindowPanel
//
//            }
//        }
        return tabsManager.createTabBuilder()
            .tabName(title)
            .createTab()
            .view
    }
}
