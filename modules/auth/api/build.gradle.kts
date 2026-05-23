plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.minecraft.brigadier)
    compileOnly(libs.minecraft.kyori.plain)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.jdbc)
    implementation(libs.klibs.kstorage)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.core)
}
