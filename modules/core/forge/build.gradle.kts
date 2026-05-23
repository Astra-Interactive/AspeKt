plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.minecraft.kyori.gson)
    compileOnly(libs.minecraft.kyori.legacy)
    compileOnly(libs.minecraft.kyori.plain)
    compileOnly(libs.minecraft.luckperms)

    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.core)

    implementation(projects.modules.core.api)
}

dependencies {
    compileOnly(
        files(
            rootProject
                .file(".gradle")
                .resolve("mavenizer")
                .resolve("repo")
                .resolve("net")
                .resolve("minecraftforge")
                .resolve("forge")
                .resolve(libs.versions.minecraft.forgeversion.get())
                .resolve("forge-${libs.versions.minecraft.forgeversion.get()}.jar")
        )
    )
    compileOnly(libs.minecraft.brigadier)
    compileOnly(libs.minecraft.forgeversion)
    compileOnly(libs.minecraft.datafixerupper)
    compileOnly(libs.minecraft.forge.bus)
    compileOnly(libs.joml)
}

configurations.runtimeElements {
    setExtendsFrom(emptySet())
}
