package org.mvnsearch.jetbrains.amber.refactoring

import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.project.Project

class AmberNamesValidator : NamesValidator {

    override fun isKeyword(name: String, project: Project?): Boolean = name in KEYWORDS

    override fun isIdentifier(name: String, project: Project?): Boolean =
        IDENTIFIER_PATTERN.matches(name) && name !in KEYWORDS

    companion object {
        private val IDENTIFIER_PATTERN = Regex("[A-Za-z_][A-Za-z_0-9]*")

        private val KEYWORDS: Set<String> = setOf(
            // Statement / declaration keywords
            "and", "as", "await", "break", "cd", "clear", "const", "continue", "cp",
            "disown", "echo", "else", "exit", "exited", "fail", "failed", "for", "from",
            "fun", "if", "import", "in", "is", "len", "let", "lines", "lock", "loop",
            "ls", "main", "mv", "nameof", "not", "or", "pid", "pub", "pwd", "ref",
            "return", "rm", "shellname", "shellversion", "silent", "sleep", "status",
            "succeeded", "sudo", "suppress", "test", "then", "touch", "trust", "unsafe",
            "while",
            // Boolean / null literals
            "true", "false", "null",
            // Built-in type names
            "Text", "Num", "Bool", "Null", "Int"
        )
    }
}
