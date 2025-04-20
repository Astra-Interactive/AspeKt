plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.forgegradle)
    kotlin("kapt")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.kstorage)
    implementation(libs.minecraft.kyori.plain)
    implementation(libs.minecraft.kyori.legacy)
    implementation(libs.minecraft.kyori.gson)

    compileOnly(libs.minecraft.luckperms)

    compileOnly(projects.modules.core.api)

    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")
}

dependencies {
    minecraft(
        "net.minecraftforge",
        "forge",
        "${libs.versions.minecraft.version.get()}-${libs.versions.minecraft.forgeversion.get()}"
    )
}
minecraft {
    mappings("official", libs.versions.minecraft.version.get())
}

configurations.runtimeElements {
    setExtendsFrom(emptySet())
}
