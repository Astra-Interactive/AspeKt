plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.rules.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    implementation(libs.minecraft.astralibs.core)

    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.serialization.json)

    testImplementation(libs.tests.kotlin.test)
}
