plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    compileOnly(libs.minecraft.paper.api)

    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.bstats)

    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)
}
