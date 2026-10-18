plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("ru.astrainteractive.gradleplugin.java.version")
    alias(libs.plugins.klibs.gradle.linter)
    id("ru.astrainteractive.gradleplugin.publication")
    id("ru.astrainteractive.gradleplugin.rootinfo")
    alias(libs.plugins.gradle.fabric.loom)
}

repositories {
    mavenLocal()
}

dependencies {
    minecraft(libs.minecraft.fabric.mojang)
    mappings(loom.officialMojangMappings())
}

dependencies {
    compileOnly(libs.guava)
    compileOnly(libs.minecraft.kyori.gson)
    compileOnly(libs.minecraft.kyori.legacy)
    compileOnly(libs.minecraft.kyori.plain)

    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.minecraft)

    implementation(projects.modules.core.api)
    implementation(projects.modules.rtp.api)

    testImplementation(libs.tests.kotlin.test)
}

dependencies {
    compileOnly(libs.minecraft.brigadier)
    compileOnly(libs.minecraft.datafixerupper)
    compileOnly(libs.joml)
}

linter {
    unitKind.set("technology")
    rootPackage.set("ru.astrainteractive.aspekt.module.rtp")
}
