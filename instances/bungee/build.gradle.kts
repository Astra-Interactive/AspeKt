import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import ru.astrainteractive.gradleplugin.model.Developer
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // Spigot dependencies
    compileOnly(libs.minecraft.bungee)
    implementation(libs.minecraft.bstats)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.command)
}
val destination = rootProject
    .layout.buildDirectory.asFile.get()
    .resolve("bungee")
    .resolve("plugins")
    .takeIf(File::exists)
    ?: rootDir.resolve("jars").also(File::mkdirs)

minecraftProcessResource {
    bukkit()
}

tasks.named<ProcessResources>("processResources").configure {
    filteringCharset = "UTF-8"
    duplicatesStrategy = DuplicatesStrategy.WARN
    filesMatching("bungee.yml") {
        expand(
            "main" to "${requireProjectInfo.group}.${requireProjectInfo.name}",
            "name" to requireProjectInfo.name,
            "prefix" to requireProjectInfo.name,
            "version" to requireProjectInfo.versionString,
            "description" to requireProjectInfo.description,
            "authors" to requireProjectInfo.developersList
                .map(Developer::id)
                .joinToString("\",\""),
        )
    }
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")
shadowJar.configure {
    mergeServiceFiles()
    dependsOn(tasks.named<ProcessResources>("processResources"))
    isReproducibleFileOrder = true
    archiveClassifier = null as String?
    archiveVersion.set(requireProjectInfo.versionString)
    archiveBaseName.set("${requireProjectInfo.name}-bungee")
    destinationDirectory = destination
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations = listOf(project.configurations.runtimeClasspath.get())
    relocationPrefix = requireProjectInfo.group
    enableRelocation = true
    minimize {
        exclude(dependency(libs.exposed.jdbc.get()))
        exclude(dependency(libs.exposed.dao.get()))
        exclude(dependency(libs.exposed.core.get()))
    }
}
