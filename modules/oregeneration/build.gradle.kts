plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.klibs.rules.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

linter {
    unit(ru.astrainteractive.klibs.rules.core.unit.model.UnitRole.Context())
    rootPackage.set("ru.astrainteractive.aspekt.module.oregeneration")
}

dependencies {
    compileOnly(libs.minecraft.paper.api)

    implementation(libs.klibs.kstorage)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.serialization.kaml)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)

    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)

    testImplementation(libs.tests.kotlin.test)
}
