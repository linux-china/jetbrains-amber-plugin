import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.intellij.platform.grammarkit")
    id("org.jetbrains.changelog")
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2026.1.2")
        testFramework(TestFrameworkType.Platform)
        bundledPlugin("org.jetbrains.plugins.terminal")
        // PsiViewer for custom language development
        plugin("PsiViewer", "2026.1")
    }
}

// Grammar-Kit Lexer generation task
tasks {
    generateLexer {
        sourceFile.set(file("src/main/grammar/Amber.flex"))
        targetOutputDir.set(file("src/main/gen/org/mvnsearch/jetbrains/amber/lexer"))
        purgeOldFiles.set(true)
    }

    generateParser {
        sourceFile.set(file("src/main/grammar/Amber.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("/org/mvnsearch/jetbrains/amber/parser/AmberParser.java")
        pathToPsiRoot.set("/org/mvnsearch/jetbrains/amber/psi")
        purgeOldFiles.set(true)
    }

    patchPluginXml {
        version = properties("version")
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            projectDir.resolve("README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider {
            with(changelog) {
                renderItem(
                    getOrNull(properties("version")) ?: getLatest(),
                    Changelog.OutputType.HTML,
                )
            }
        })
    }
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        freeCompilerArgs.set(listOf("-XXLanguage:+MultiDollarInterpolation"))
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/gen")
    }
}

