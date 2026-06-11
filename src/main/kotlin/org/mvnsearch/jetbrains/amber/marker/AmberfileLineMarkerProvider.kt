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

    override fun getLineMarkerInfo(functionDef: PsiElement): LineMarkerInfo<*>? {
        if (functionDef !is AmberFunctionDef) return null
        val file = functionDef.containingFile?.originalFile ?: return null
        // only Amberfile
        if (file.name != "Amberfile") return null
        // only pub functions
        if (functionDef.visibility == null) return null
        val functionName = functionDef.functionName.text ?: return null
        if (functionDef.parameterList.isNotEmpty()) return null

        return LineMarkerInfo(
            functionDef,
            functionDef.textRange,
            AllIcons.Actions.Execute,
            { "Run $functionName Task" },
            { _, _ -> runFunction(file, functionName) },
            GutterIconRenderer.Alignment.LEFT,
            { "Run $functionName Task" }
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
