plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.forgegradle)
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)

    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.forge)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    compileOnly(libs.kyori.plain)
    compileOnly(libs.kyori.legacy)
    compileOnly(libs.kyori.gson)
    // Test

    testImplementation(libs.tests.kotlin.test)
    // Local
    implementation(projects.modules.core.api)
    implementation(projects.modules.core.forge)
    implementation(projects.modules.sethome.api)
}

dependencies {
    minecraft(
        "net.minecraftforge",
        "forge",
        "${libs.versions.minecraft.mojang.version.get()}-${libs.versions.minecraft.forgeversion.get()}"
    )
}
minecraft {
    mappings("official", libs.versions.minecraft.mojang.version.get())
}

configurations.runtimeElements {
    setExtendsFrom(emptySet())
}
