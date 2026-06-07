package org.mvnsearch.jetbrains.amber

import com.intellij.icons.AllIcons
import com.intellij.testFramework.LightVirtualFile
import org.mvnsearch.jetbrains.amber.MemberItem.Companion.f
import java.util.concurrent.ConcurrentHashMap
import javax.swing.Icon

data class MemberItem(val name: String, val type: String, val icon: Icon? = null) {
    companion object {
        fun f(name: String): MemberItem {
            return MemberItem(name, "fun", AllIcons.Nodes.Function)
        }

        fun v(name: String): MemberItem {
            return MemberItem(name, "variable", AllIcons.Nodes.Variable)
        }
    }
}

val STD_ARRAY_MEMBERS = listOf(
    f("array_contains"),
    f("array_extract_at"),
    f("array_filled"),
    f("array_find"),
    f("array_find_all"),
    f("array_first"),
    f("array_last"),
    f("array_pop"),
    f("array_shift"),
    f("sort"),
    f("sorted"),
)

val STD_DATE_MEMBERS = listOf(
    f("date_add"),
    f("date_format_posix"),
    f("date_from_posix"),
    f("date_now"),
    f("date_sub"),
)

val STD_ENV_MEMBERS = listOf(
    f("bold"),
    f("echo_colored"),
    f("echo_error"),
    f("echo_info"),
    f("echo_success"),
    f("echo_warning"),
    f("env_const_set"),
    f("env_file_load"),
    f("env_var_get"),
    f("env_var_load"),
    f("env_var_set"),
    f("env_var_test"),
    f("env_var_unset"),
    f("escaped"),
    f("has_failed"),
    f("input_confirm"),
    f("input_hidden"),
    f("input_prompt"),
    f("is_command"),
    f("is_root"),
    f("italic"),
    f("kill"),
    f("mount"),
    f("pgrep"),
    f("pgrep_exact"),
    f("pkill"),
    f("pkill_exact"),
    f("pkill_force"),
    f("printf"),
    f("shopt_disable"),
    f("shopt_enable"),
    f("styled"),
    f("umount"),
    f("umount_force"),
    f("uname_all"),
    f("uname_kernel_name"),
    f("uname_kernel_release"),
    f("uname_kernel_version"),
    f("uname_machine"),
    f("uname_nodename"),
    f("uname_os"),
    f("underlined"),
    f("bold"),
    f("bold"),
    f("bold"),
    f("bold"),
    f("bold"),
)

val STD_FS_MEMBERS = listOf(
    f("dir_create"),
    f("dir_exists"),
    f("file_append"),
    f("file_chmod"),
    f("file_chown"),
    f("file_compress"),
    f("file_exists"),
    f("file_extract"),
    f("file_glob"),
    f("file_glob_all"),
    f("file_read"),
    f("file_write"),
    f("is_mac_os_mktemp"),
    f("symlink_create"),
    f("temp_dir_create"),
)

val STD_HTTP_MEMBERS = listOf(
    f("fetch"),
    f("file_download"),
)

val STD_MATH_MEMBERS = listOf(
    f("math_abs"),
    f("math_ceil"),
    f("math_floor"),
    f("math_round"),
    f("math_sum"),
)

val STD_TEXT_MEMBERS = listOf(
    f("capitalized"),
    f("char_at"),
    f("count_chars"),
    f("count_lines"),
    f("count_words"),
    f("cpad"),
    f("ends_with"),
    f("join"),
    f("lowercase"),
    f("lpad"),
    f("match_regex"),
    f("match_regex_any"),
    f("parse_int"),
    f("parse_num"),
    f("replace"),
    f("replace_one"),
    f("replace_regex"),
    f("reversed"),
    f("rpad"),
    f("sed_version"),
    f("slice"),
    f("sort_lines"),
    f("split"),
    f("split_chars"),
    f("split_lines"),
    f("split_words"),
    f("starts_with"),
    f("text_contains"),
    f("text_contains_all"),
    f("text_contains_any"),
    f("trim"),
    f("trim_left"),
    f("trim_right"),
    f("uniq_lines"),
    f("uppercase"),
    f("zfill"),
)

val STD_TEST_MEMBERS = listOf(
    f("assert"),
    f("assert_eq"),
)

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

    /**
     * Inverse of [findMembers]: given a bare symbol name, return the `std/<lib>` path
     * that exports it (or `null` if no std library exports a member with this name).
     * Used by the auto-import quick fix to suggest the right `from "std/<lib>"`.
     */
    fun findContainingLib(name: String): String? {
        for (libPath in STD_LIB_NAMES) {
            if (findMembers(libPath).any { it.name == name }) return libPath
        }
        return null
    }

    fun findMembers(path: String): List<MemberItem> {
        return when (path) {
            "std/array", "array" -> STD_ARRAY_MEMBERS
            "std/test", "test" -> STD_TEST_MEMBERS
            "std/text", "text" -> STD_TEXT_MEMBERS
            "std/math", "math" -> STD_MATH_MEMBERS
            "std/date", "date" -> STD_DATE_MEMBERS
            "std/env", "env" -> STD_ENV_MEMBERS
            "std/http", "http" -> STD_HTTP_MEMBERS
            "std/fs", "fs" -> STD_FS_MEMBERS
            else -> emptyList()
        }
    }
}
