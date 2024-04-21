
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo
import ru.astrainteractive.gradleplugin.setupVelocityProcessor

plugins {
    kotlin("jvm")
    alias(libs.plugins.gradle.buildconfig)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // Velocity
    compileOnly(libs.minecraft.velocity.api)
    annotationProcessor(libs.minecraft.velocity.api)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.klibs.kdi)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(libs.minecraft.mockbukkit)
}

buildConfig {
    val requireProjectInfo = requireProjectInfo
    className("BuildKonfig")
    packageName(requireProjectInfo.group)
    fun buildConfigStringField(name: String, value: String) {
        buildConfigField("String", name, "\"${value}\"")
    }
    buildConfigStringField("id", requireProjectInfo.name.toLowerCase())
    buildConfigStringField("name", requireProjectInfo.name)
    buildConfigStringField("version", requireProjectInfo.versionString)
    buildConfigStringField("url", requireProjectInfo.url)
    buildConfigStringField("description", requireProjectInfo.description)
    buildConfigStringField("author", requireProjectInfo.developersList.first().id)
}

setupVelocityProcessor()
