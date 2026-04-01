plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.minecraft.kyori.gson)
    compileOnly(libs.minecraft.kyori.legacy)
    compileOnly(libs.minecraft.kyori.plain)

    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.neoforge)

    implementation(projects.modules.auth.api)
    implementation(projects.modules.core.api)
    implementation(projects.modules.core.neoforge)
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
    compileOnly("com.mojang:brigadier:1.0.500")
    compileOnly("com.mojang:authlib:6.0.54")
    compileOnly("net.neoforged:bus:8.0.5")
}
