package org.mvnsearch.jetbrains.amber.notification

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.EditorNotifications
import org.mvnsearch.jetbrains.amber.AmberFileType
import java.util.function.Function
import javax.swing.JComponent

class AmberInterpreterNotificationProvider : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?>? {
        if (file.fileType != AmberFileType) return null
        if (AmberInterpreterDetector.isAvailable()) return null
        return Function { _ -> createPanel(project) }
    }

    private fun createPanel(project: Project): JComponent {
        val panel = EditorNotificationPanel(EditorNotificationPanel.Status.Warning)
        panel.text = "Amber binary not on PATH — run configurations and the gutter run icons will fail."
        panel.createActionLabel("Install Amber…") {
            BrowserUtil.browse(AMBER_INSTALL_URL)
        }
        panel.createActionLabel("I've installed it — recheck") {
            AmberInterpreterDetector.refresh()
            EditorNotifications.getInstance(project).updateAllNotifications()
        }
        return panel
    }

    companion object {
        private const val AMBER_INSTALL_URL = "https://docs.amber-lang.com/getting_started/installation"
    }
}
