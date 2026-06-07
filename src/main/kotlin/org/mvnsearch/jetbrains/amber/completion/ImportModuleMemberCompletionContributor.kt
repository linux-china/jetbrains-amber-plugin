package org.mvnsearch.jetbrains.amber.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.platform.ide.progress.ModalTaskOwner.project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.intellij.util.text.trimMiddle
import org.mvnsearch.jetbrains.amber.AmberLanguage
import org.mvnsearch.jetbrains.amber.AmberStdLibrary
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionDef
import org.mvnsearch.jetbrains.amber.psi.AmberImportId
import org.mvnsearch.jetbrains.amber.psi.AmberImportIds
import org.mvnsearch.jetbrains.amber.psi.AmberImportPath
import org.mvnsearch.jetbrains.amber.psi.AmberNamedElement
import org.mvnsearch.jetbrains.amber.psi.AmberTypes
import kotlin.jvm.java

class ImportModuleMemberCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            psiElement(PsiElement::class.java)
                .withElementType(AmberTypes.IDENTIFIER)
                .withParent(AmberImportId::class.java)
                .withLanguage(AmberLanguage),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val element = parameters.position
                    val importStatement = PsiTreeUtil.getParentOfType(element, AmberImportIds::class.java) ?: return
                    val importedIdList = importStatement.importIdList.map { it.text.trim() }
                    val importPath = importStatement.importPath.text.trim('"')
                    if (importPath.isEmpty()) return
                    if (importPath.startsWith("./")) {
                        element.containingFile?.originalFile?.virtualFile?.parent?.let { dir ->
                            dir.findChild(importPath.removePrefix("./"))?.let { amberFile ->
                                addPubElements(element.project, amberFile, result, importedIdList)
                            }
                        }
                    } else if (importPath.startsWith("std/")) {
                        AmberStdLibrary.findMembers(importPath).forEach { member ->
                            result.addElement(LookupElementBuilder.create(member.name).withIcon(member.icon))
                        }
                    }
                }
            }
        )
    }

    fun addPubElements(
        project: Project,
        amberFile: VirtualFile,
        result: CompletionResultSet,
        importedIdList: List<String>
    ) {
        PsiManager.getInstance(project).findFile(amberFile)?.let { psiFile ->
            if (psiFile is AmberFile) {
                psiFile.getPubElements().forEach { namedElement ->
                    val idName = namedElement.name!!
                    if (!importedIdList.contains(idName)) {
                        val icon = if (namedElement is AmberFunctionDef) {
                            AllIcons.Nodes.Function
                        } else {
                            AllIcons.Nodes.Variable
                        }
                        result.addElement(LookupElementBuilder.create(idName).withIcon(icon))
                    }
                }
            }
        }
    }
}

