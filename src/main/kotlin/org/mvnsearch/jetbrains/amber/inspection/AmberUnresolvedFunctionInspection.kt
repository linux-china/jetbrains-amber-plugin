package org.mvnsearch.jetbrains.amber.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import org.mvnsearch.jetbrains.amber.AmberStdLibrary
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import org.mvnsearch.jetbrains.amber.psi.AmberFunctionCall
import org.mvnsearch.jetbrains.amber.psi.AmberTypes
import org.mvnsearch.jetbrains.amber.psi.AmberVisitor

/**
 * Flags `foo(…)` call sites where `foo` is an unresolved identifier and happens to
 * be exported by one of the bundled `std/…` libraries. Offers [AmberAutoImportQuickFix]
 * to add the matching `import { foo } from "std/<lib>"`.
 *
 * Built-in commands that are spelled as keywords (`cd`, `echo`, `len`, …) come through
 * `function_name` as keyword tokens rather than IDENTIFIERs; those don't need an
 * import and are skipped.
 */
class AmberUnresolvedFunctionInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : AmberVisitor() {
            override fun visitFunctionCall(call: AmberFunctionCall) {
                val funName = call.functionName ?: return
                // Skip builtin keywords used as function-call heads (cd, echo, len, …)
                val identifierNode = funName.node?.findChildByType(AmberTypes.IDENTIFIER) ?: return
                val name = identifierNode.text
                val file = call.containingFile as? AmberFile ?: return

                if (isResolved(name, file)) return

                val libPath = AmberStdLibrary.findContainingLib(name) ?: return

                holder.registerProblem(
                    funName,
                    "Unresolved function '$name' — available in $libPath",
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                    AmberAutoImportQuickFix(name, libPath)
                )
            }
        }
    }

    private fun isResolved(name: String, file: AmberFile): Boolean {
        if (file.findNamedElement(name) != null) return true
        if (name in file.getImportedIds()) return true
        return false
    }
}
