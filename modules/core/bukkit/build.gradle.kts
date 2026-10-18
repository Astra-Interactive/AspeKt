plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    api(libs.klibs.kstorage)

    compileOnly(libs.minecraft.discordsrv)
    compileOnly(libs.minecraft.paper.api)
    compileOnly(libs.minecraft.vaultapi)

    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.menu.bukkit)

    implementation(projects.modules.core.api)
}

linter {
    unitKind.set("technology")
}
