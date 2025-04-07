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
    implementation(projects.modules.core)
    implementation(projects.modules.coreBukkit)
    implementation(projects.modules.menu)
    implementation(projects.modules.broadcast)
    implementation(projects.modules.adminprivate)
    implementation(projects.modules.discordlink)
    implementation(projects.modules.townyDiscord)
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
    implementation(projects.modules.entities)
    implementation(projects.modules.jail)
    implementation(projects.modules.invisibleItemFrames)
}
val destination = File("/home/makeevrserg/Desktop/git/AspeKt/build/bukkit/plugins/")
    .takeIf(File::exists)
    ?: File(rootDir, "jars").also(File::mkdirs)

minecraftProcessResource {
    bukkit()
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")
shadowJar.configure {
    mergeServiceFiles()
    mustRunAfter(minecraftProcessResource.task)
    dependsOn(minecraftProcessResource.task)
    isReproducibleFileOrder = true
    archiveClassifier = null as String?
    archiveVersion.set(requireProjectInfo.versionString)
    archiveBaseName.set("${requireProjectInfo.name}-bukkit")
    destinationDirectory = destination
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//    configurations = listOf(project.configurations.shadow.get())
//    dependsOn(configurations)
    relocationPrefix = requireProjectInfo.group
    enableRelocation = true
    minimize {
        exclude(dependency(libs.exposed.jdbc.get()))
        exclude(dependency(libs.exposed.dao.get()))
        exclude(dependency(libs.exposed.core.get()))
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.version.get()}"))
    }
}
