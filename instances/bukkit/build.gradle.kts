import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.shadow)
//    alias(libs.plugins.klibs.minecraft.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.exposed)
    compileOnly(libs.minecraft.vaultapi)
    compileOnly(libs.driver.h2)
    compileOnly(libs.driver.jdbc)
    compileOnly(libs.driver.mysql)
    // Spigot
    compileOnly(libs.minecraft.luckperms)
    compileOnly(libs.minecraft.discordsrv)
    compileOnly(libs.minecraft.essentialsx)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)
    implementation(projects.modules.menu)
    implementation(projects.modules.broadcast)
    implementation(projects.modules.claims.api)
    implementation(projects.modules.claims.bukkit)
    implementation(projects.modules.moneydrop)
    implementation(projects.modules.moneyAdvancements)
    implementation(projects.modules.autocrop)
    implementation(projects.modules.newbee)
    implementation(projects.modules.antiswear)
    implementation(projects.modules.chatgame)
    implementation(projects.modules.economy)
    implementation(projects.modules.treecapitator)
    implementation(projects.modules.inventorysort)
    implementation(projects.modules.sit)
    implementation(projects.modules.restrictions)
    implementation(projects.modules.jail)
    implementation(projects.modules.invisibleItemFrames)
}
val destination = rootProject
    .layout.buildDirectory.asFile.get()
    .resolve("bukkit")
    .resolve("plugins")
    .takeIf(File::exists)
    ?: rootDir.resolve("jars").also(File::mkdirs)

minecraftProcessResource {
    bukkit()
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")
shadowJar.configure {
    mergeServiceFiles()
    dependsOn(tasks.named<ProcessResources>("processResources"))
    isReproducibleFileOrder = true
    archiveClassifier = null as String?
    archiveVersion.set(requireProjectInfo.versionString)
    archiveBaseName.set("${requireProjectInfo.name}-bukkit")
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
