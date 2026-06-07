package org.mvnsearch.jetbrains.amber.notification

import com.intellij.openapi.util.SystemInfo
import java.io.File

/**
 * Cached check for whether the `amber` binary is reachable on the user's PATH.
 *
 * The PATH lookup is cheap but called from a notification provider that can fire
 * many times — so the result is memoized until [refresh] is invoked (typically
 * from the "Recheck" link in the editor banner).
 */
object AmberInterpreterDetector {

    @Volatile
    private var cached: Boolean? = null

    fun isAvailable(): Boolean = cached ?: detect().also { cached = it }

    fun refresh() {
        cached = null
    }

    private fun detect(): Boolean {
        val pathEnv = System.getenv("PATH") ?: return false
        val executable = if (SystemInfo.isWindows) "amber.exe" else "amber"
        return pathEnv.split(File.pathSeparator).any { dir ->
            if (dir.isBlank()) return@any false
            val candidate = File(dir, executable)
            candidate.isFile && candidate.canExecute()
        }
    }
}
