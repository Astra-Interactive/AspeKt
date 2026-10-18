plugins {
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.klibs.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.tests.kotlin.test)
}
