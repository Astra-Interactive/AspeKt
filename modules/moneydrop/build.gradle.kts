plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.exposed)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.vaultapi)
    // Spigot
    compileOnly(libs.minecraft.essentialsx)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(libs.minecraft.mockbukkit)
    implementation(projects.modules.core)
}
