package org.mvnsearch.jetbrains.amber.marker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberTypes
import org.mvnsearch.jetbrains.amber.terminal.AmberTerminalService

class AmberfileLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.node?.elementType != AmberTypes.FUN_KW) return null
        val file = element.containingFile?.originalFile ?: return null
        val functionDef = element.parent as? AmberFunctionDef ?: return null
        // only pub functions
        if (functionDef.visibility == null) return null
        val functionName = functionDef.functionName.text ?: return null
        if (file.virtualFile == null) return null
        // only Amberfile
        if (file.name != "Amberfile") return null

        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.RunConfigurations.TestState.Run,
            { "Run $functionName" },
            { _, _ -> runFunction(file, functionName) },
            GutterIconRenderer.Alignment.LEFT,
            { "Run $functionName" }
        )
    }

    private fun runFunction(file: PsiFile, functionName: String) {
        val project = file.project
        val virtualFile = file.virtualFile ?: return
        val projectDir = project.guessProjectDir()
        val relativePath = projectDir?.let { VfsUtilCore.getRelativePath(virtualFile, it) } ?: virtualFile.path
        val command = "amber eval 'import { $functionName } from \"./$relativePath\"; $functionName()'\n"
        val service = project.getService(AmberTerminalService::class.java) ?: return
        val view = service.getOrCreateTerminalView(file.name)
        view.executeCommand(command)
    }
}
