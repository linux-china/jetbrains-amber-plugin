package org.mvnsearch.jetbrains.amber.check

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import org.mvnsearch.jetbrains.amber.notification.AmberInterpreterDetector
import org.mvnsearch.jetbrains.amber.psi.AmberFile
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Surfaces diagnostics from `amber check <file>` as editor squigglies.
 *
 * Flow:
 *  1. [collectInformation] — EDT — capture the on-disk file path and project base dir,
 *     bail when the file isn't an Amber source on the local filesystem, when `amber`
 *     isn't on PATH, or when the file currently has unrecoverable syntax errors.
 *  2. [doAnnotate] — background — shell out to `amber check`, parse the output into
 *     [Diagnostic]s. Respects `ProgressManager.checkCanceled()` between lines.
 *  3. [apply] — EDT — turn each diagnostic into a platform [AnnotationHolder] entry
 *     scoped to the PSI element at the reported `(line, column)`.
 */
class AmberExternalAnnotator :
    ExternalAnnotator<AmberExternalAnnotator.CollectedInfo, List<AmberExternalAnnotator.Diagnostic>>() {

    data class CollectedInfo(val virtualFile: VirtualFile, val workingDir: String)

    data class Diagnostic(
        val line: Int,
        val column: Int,
        val severity: HighlightSeverity,
        val message: String
    )

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): CollectedInfo? {
        if (hasErrors) return null
        if (file !is AmberFile) return null
        if (!AmberInterpreterDetector.isAvailable()) return null
        val vf = file.virtualFile ?: return null
        if (!vf.isInLocalFileSystem) return null
        val workingDir = file.project.basePath
            ?: vf.parent?.path
            ?: return null
        return CollectedInfo(vf, workingDir)
    }

    override fun doAnnotate(collectedInfo: CollectedInfo?): List<Diagnostic>? {
        val info = collectedInfo ?: return null
        ProgressManager.checkCanceled()

        val process = try {
            val canonicalFilePath = info.virtualFile.canonicalPath
            ProcessBuilder("amber", "check", canonicalFilePath)
                .directory(File(info.workingDir))
                .redirectErrorStream(true)
                .start()
        } catch (e: Exception) {
            return emptyList()
        }

        val output = StringBuilder()
        process.inputStream.bufferedReader(Charsets.UTF_8).useLines { lines ->
            for (line in lines) {
                ProgressManager.checkCanceled()
                output.appendLine(line)
            }
        }
        if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return emptyList()
        }

        return parseDiagnostics(output.toString(), info.virtualFile.path)
    }

    override fun apply(file: PsiFile, annotationResult: List<Diagnostic>?, holder: AnnotationHolder) {
        val results = annotationResult ?: return
        if (results.isEmpty()) return
        val document = file.viewProvider.document ?: return
        val lineCount = document.lineCount

        for (diag in results) {
            val zeroBasedLine = (diag.line - 1).coerceIn(0, (lineCount - 1).coerceAtLeast(0))
            val lineStart = document.getLineStartOffset(zeroBasedLine)
            val lineEnd = document.getLineEndOffset(zeroBasedLine)
            val rawCol = (diag.column - 1).coerceAtLeast(0)
            val startOffset = (lineStart + rawCol).coerceAtMost(lineEnd)
            val endOffset = tokenEnd(file, startOffset, lineEnd)
            val range = TextRange(startOffset, endOffset)

            holder.newAnnotation(diag.severity, diag.message)
                .range(range)
                .create()
        }
    }

    private fun tokenEnd(file: PsiFile, offset: Int, lineEnd: Int): Int {
        if (offset >= lineEnd) return (offset + 1).coerceAtMost(lineEnd)
        val element = file.findElementAt(offset)
        return element?.textRange?.endOffset?.coerceAtMost(lineEnd) ?: (offset + 1).coerceAtMost(lineEnd)
    }

    /**
     * Parses `amber check` output. The diagnostic Amber emits is two-line:
     *
     *  ```
     *   ERROR  Expected both operands to be of the same type, but got 'Text' and 'Int'.
     *  at hello.ab:10:10
     *
     *  9 | main {
     *  10|     echo("Jackie" + 2)
     *  11| }
     *  ```
     *
     *  Line 1 carries the severity and message; the following non-blank line carries
     *  the file:line:column location prefixed by `at`. The numbered source-preview
     *  lines underneath are ignored. Multiple diagnostics in one run are handled by
     *  scanning the entire output.
     */
    private fun parseDiagnostics(output: String, filePath: String): List<Diagnostic> {
        if (output.isBlank()) return emptyList()
        val results = mutableListOf<Diagnostic>()
        val fileBaseName = File(filePath).name
        val lines = output.lines()

        var i = 0
        while (i < lines.size) {
            val severityMatch = AMBER_SEVERITY_PATTERN.matchEntire(lines[i])
            if (severityMatch == null) {
                i++
                continue
            }
            val severityWord = severityMatch.groupValues[1]
            val message = severityMatch.groupValues[2].trim()

            // Locate the next non-blank line and try to read `at <path>:<line>:<col>`.
            var j = i + 1
            while (j < lines.size && lines[j].isBlank()) j++
            val atMatch = if (j < lines.size) AMBER_AT_PATTERN.matchEntire(lines[j].trim()) else null
            if (atMatch == null) {
                i++
                continue
            }

            val path = atMatch.groupValues[1]
            val ln = atMatch.groupValues[2].toIntOrNull()
            val col = atMatch.groupValues[3].toIntOrNull()
            if (ln == null || col == null) {
                i = j + 1
                continue
            }
            // Filter out diagnostics that refer to a different file (e.g., imported modules).
            if (path != filePath && !path.endsWith(fileBaseName)) {
                i = j + 1
                continue
            }
            results.add(
                Diagnostic(
                    line = ln,
                    column = col,
                    severity = severityFromWord(severityWord),
                    message = message
                )
            )
            i = j + 1
        }
        return results
    }

    private fun severityFromWord(word: String): HighlightSeverity = when (word.lowercase()) {
        "error", "fatal" -> HighlightSeverity.ERROR
        "warning", "warn" -> HighlightSeverity.WARNING
        "info", "note", "hint" -> HighlightSeverity.INFORMATION
        else -> HighlightSeverity.WARNING
    }

    companion object {
        private const val PROCESS_TIMEOUT_SECONDS = 15L

        // Line 1 of an Amber diagnostic: optional leading whitespace, the severity word, then the message.
        // Tolerant of any amount of horizontal whitespace between severity and message.
        private val AMBER_SEVERITY_PATTERN =
            Regex(
                """^\s*(ERROR|WARNING|WARN|INFO|NOTE|HINT|FATAL)\s+(\S.*?)\s*$""",
                RegexOption.IGNORE_CASE
            )

        // Line 2: "at <path>:<line>:<col>"
        private val AMBER_AT_PATTERN =
            Regex(
                """^at\s+(.+?):(\d+):(\d+)\s*$""",
                RegexOption.IGNORE_CASE
            )
    }
}
