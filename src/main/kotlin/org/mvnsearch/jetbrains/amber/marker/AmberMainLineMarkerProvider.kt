package org.mvnsearch.jetbrains.amber.marker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.mvnsearch.jetbrains.amber.psi.AmberMainDef
import org.mvnsearch.jetbrains.amber.psi.AmberShebangLine
import org.mvnsearch.jetbrains.amber.psi.AmberTypes
import org.mvnsearch.jetbrains.amber.terminal.AmberTerminalService
import java.util.function.Supplier

class AmberMainLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.node?.elementType != AmberTypes.MAIN_KW) return null
        val mainDef = element.parent as? AmberMainDef ?: return null
        val file = mainDef.containingFile?.originalFile ?: return null
        if (file.virtualFile == null) return null
        if (hasShebang(file)) return null

        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.RunConfigurations.TestState.Run,
            { "Run Amber Script" },
            { _, _ -> runScript(file) },
            GutterIconRenderer.Alignment.LEFT,
            { "Run Amber Script" }
        )
    }

    private fun hasShebang(file: PsiFile): Boolean =
        PsiTreeUtil.getChildOfType(file, AmberShebangLine::class.java) != null

    private fun runScript(file: PsiFile) {
        val project = file.project
        val virtualFile = file.virtualFile ?: return
        val projectDir = project.guessProjectDir()
        val relativePath = projectDir?.let { VfsUtilCore.getRelativePath(virtualFile, it) } ?: virtualFile.path

        val service = project.getService(AmberTerminalService::class.java) ?: return
        val terminalWidget = service.getOrCreateTerminalView(file.name)
        terminalWidget.executeCommand("amber $relativePath\n")
    }
}