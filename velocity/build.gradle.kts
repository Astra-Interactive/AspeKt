
import ru.astrainteractive.gradleplugin.setupVelocityProcessor
import ru.astrainteractive.gradleplugin.util.ProjectProperties.projectInfo

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
    implementation(klibs.klibs.kdi)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(libs.minecraft.mockbukkit)
}

buildConfig {
    val projectInfo = projectInfo
    className("BuildKonfig")
    packageName(projectInfo.group)
    fun buildConfigStringField(name: String, value: String) {
        buildConfigField("String", name, "\"${value}\"")
    }
    buildConfigStringField("id", projectInfo.name.toLowerCase())
    buildConfigStringField("name", projectInfo.name)
    buildConfigStringField("version", projectInfo.versionString)
    buildConfigStringField("url", projectInfo.url)
    buildConfigStringField("description", projectInfo.description)
    buildConfigStringField("author", projectInfo.developersList.first().id)
}

setupVelocityProcessor()
