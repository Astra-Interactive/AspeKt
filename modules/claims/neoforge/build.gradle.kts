plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.coroutines.core)

    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.neoforge)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    compileOnly(libs.minecraft.kyori.plain)
    compileOnly(libs.minecraft.kyori.legacy)
    compileOnly(libs.minecraft.kyori.gson)
    // Test

    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.core.api)
    implementation(projects.modules.core.neoforge)
    implementation(projects.modules.claims.api)
}

dependencies {
    compileOnly(
        files(
            rootProject.project(projects.instances.neoforge.path)
                .file(".gradle")
                .resolve("repositories")
                .resolve("ng_dummy_ng")
                .resolve("net")
                .resolve("neoforged")
                .resolve("neoforge")
                .resolve(libs.versions.minecraft.neoforgeversion.get())
                .resolve("neoforge-${libs.versions.minecraft.neoforgeversion.get()}.jar")
        )
    )
    compileOnly(libs.minecraft.neoforgeversion)
    compileOnly("org.joml:joml:1.10.8")
    compileOnly("com.mojang:datafixerupper:8.0.16")
    compileOnly("com.mojang:brigadier:1.3.10")
    compileOnly("com.mojang:authlib:6.0.54")
    compileOnly("net.neoforged:bus:8.0.2")
}
