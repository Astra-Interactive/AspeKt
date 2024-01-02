plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    compileOnly("com.discordsrv:discordsrv:1.25.0")
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.minecraft.astralibs.orm)
    implementation(klibs.klibs.kdi)
    implementation(libs.minecraft.astralibs.spigot.gui)
    implementation(libs.minecraft.astralibs.spigot.core)
}
