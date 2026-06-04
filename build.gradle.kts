import org.jetbrains.intellij.platform.gradle.TestFrameworkType

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

