package org.mvnsearch.jetbrains.amber

import com.intellij.testFramework.LightVirtualFile
import java.util.concurrent.ConcurrentHashMap

object AmberStdLibrary {

    private const val RESOURCE_PREFIX = "/amber/std/"
    private val cache = ConcurrentHashMap<String, LightVirtualFile>()
    val STD_LIB_NAMES =
        listOf("std/array", "std/date", "std/env", "std/fs", "std/http", "std/math", "std/test", "std/text")

    fun find(relativePath: String): LightVirtualFile? {
        val key = normalize(relativePath)
        cache[key]?.let { return it }

        val url = AmberStdLibrary::class.java.getResource("$RESOURCE_PREFIX$key") ?: return null
        val content = url.openStream().bufferedReader(Charsets.UTF_8).use { it.readText() }
        val file = LightVirtualFile(key.substringAfterLast('/'), AmberFileType, content).apply {
            isWritable = false
        }
        return cache.putIfAbsent(key, file) ?: file
    }

    private fun normalize(path: String): String = if (path.endsWith(".ab")) path else "$path.ab"
}
