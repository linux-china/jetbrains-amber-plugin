package org.mvnsearch.jetbrains.amber.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.SystemInfo
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import org.mvnsearch.jetbrains.amber.AmberFileType
import org.mvnsearch.jetbrains.amber.AmberIcons
import java.io.File

class NewAmberFileAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val view = e.getData(LangDataKeys.IDE_VIEW) ?: return
        val dir = view.orChooseDirectory ?: return

        val input = Messages.showInputDialog(
            project,
            "Enter file name:",
            "New Amber File",
            AmberIcons.FILE,
            "",
            null
        )?.trim()?.takeIf { it.isNotEmpty() } ?: return

        val fileName = if (input.endsWith(".ab")) input else "$input.ab"

        if (dir.findFile(fileName) != null) {
            Messages.showErrorDialog(project, "File '$fileName' already exists.", "New Amber File")
            return
        }

        val content = """
            |#!/usr/bin/env amber
            |
            |main {
            |    echo("Hello, Amber!")
            |}
            |""".trimMargin()

        WriteCommandAction.runWriteCommandAction(project) {
            val psiFile = PsiFileFactory.getInstance(project)
                .createFileFromText(fileName, AmberFileType, content)
            val created = dir.add(psiFile) as PsiFile

            if (!SystemInfo.isWindows) {
                val virtualFile = created.virtualFile
                if (virtualFile != null) {
                    File(virtualFile.path).setExecutable(true, false)
                    WriteAction.run<RuntimeException> { virtualFile.refresh(false, false) }
                }
            }

            view.selectElement(created)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible =
            e.project != null && e.getData(LangDataKeys.IDE_VIEW) != null
    }
}
