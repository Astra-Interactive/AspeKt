plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    compileOnly(libs.minecraft.packetevents)
    compileOnly(libs.minecraft.paper.api)

    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)

    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)

    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.minecraft.paper.api)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(libs.tests.mockito)
}
