package org.mvnsearch.jetbrains.amber.template

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import org.mvnsearch.jetbrains.amber.AmberFileType

class AmberTemplateContextType : TemplateContextType("Amber") {
    override fun isInContext(context: TemplateActionContext): Boolean =
        context.file.fileType == AmberFileType
}
