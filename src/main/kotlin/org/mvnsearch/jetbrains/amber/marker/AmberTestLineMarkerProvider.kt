package org.mvnsearch.jetbrains.amber.marker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.mvnsearch.jetbrains.amber.psi.AmberTestDef
import org.mvnsearch.jetbrains.amber.psi.AmberTypes
import org.mvnsearch.jetbrains.amber.terminal.AmberTerminalService
import java.util.function.Supplier

class AmberTestLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.node?.elementType != AmberTypes.TEST_KW) return null
        val testDef = element.parent as? AmberTestDef ?: return null
        val file = testDef.containingFile?.originalFile ?: return null
        if (file.virtualFile == null) return null

        return LineMarkerInfo(
            element,
            element.textRange,
            AllIcons.RunConfigurations.TestState.Run,
            { "Run Amber Test" },
            { _, _ -> runTest(file, testDef) },
            GutterIconRenderer.Alignment.LEFT,
            { "Run Amber Test" }
        )
    }

    private fun runTest(file: PsiFile, testDef: AmberTestDef) {
        val project = file.project
        val virtualFile = file.virtualFile ?: return
        val projectDir = project.guessProjectDir()
        val relativePath = projectDir?.let { VfsUtilCore.getRelativePath(virtualFile, it) } ?: virtualFile.path

        val testName = testDef.testName?.text?.let(::unquote)
        val command = if (testName.isNullOrEmpty()) {
            "amber test $relativePath\n"
        } else {
            "amber test $relativePath --test-case \"$testName\"\n"
        }

        val service = project.getService(AmberTerminalService::class.java) ?: return
        val view = service.getOrCreateTerminalView(file.name)
        view.executeCommand(command)
    }

    private fun unquote(text: String): String =
        if (text.length >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
            text.substring(1, text.length - 1)
        } else {
            text
        }
}
