plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.rules.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly(libs.minecraft.paper.api)

    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)

    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)
}
