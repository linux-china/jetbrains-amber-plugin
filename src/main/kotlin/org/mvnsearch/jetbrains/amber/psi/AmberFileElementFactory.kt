package org.mvnsearch.jetbrains.amber.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import org.mvnsearch.jetbrains.amber.AmberFileType

object AmberFileElementFactory {

    fun createFile(project: Project, text: String) =
        PsiFileFactory.getInstance(project).createFileFromText("__amber_1.ab", AmberFileType, text) as AmberFile


    fun createFunctionDef(project: Project, functionName: String): AmberFunctionDef {
        return createFile(project, "fun ${functionName}{}").firstChild as AmberFunctionDef
    }

    fun createAmberVariableInitConst(project: Project, variableName: String): AmberVariableInitConst {
        return createFile(project, "const $variableName = 0").firstChild as AmberVariableInitConst
    }

    fun createAmberVariableInitMut(project: Project, variableName: String): AmberVariableInitMut {
        return createFile(project, "let $variableName = 0").firstChild as AmberVariableInitMut
    }

    fun createFunctionName(project: Project, name: String): AmberFunctionName =
        createFunctionDef(project, name).functionName!!

    fun createVariableName(project: Project, name: String): AmberVariableName =
        createAmberVariableInitMut(project, name).variableName

    fun createParameterName(project: Project, name: String): AmberParameterName {
        val file = createFile(project, "fun __amber_dummy($name) {}")
        val def = file.firstChild as AmberFunctionDef
        val parameter = com.intellij.psi.util.PsiTreeUtil.findChildOfType(def, AmberParameter::class.java)
            ?: error("Could not parse parameter for name '$name'")
        return parameter.parameterName
    }
}