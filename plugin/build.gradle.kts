import ru.astrainteractive.gradleplugin.setupSpigotProcessor
import ru.astrainteractive.gradleplugin.setupSpigotShadow

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    // Kotlin
    implementation(libs.bundles.kotlin)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    // AstraLibs
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.klibs.kdi)
    implementation(libs.klibs.mikro.core)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.vaultapi)
    // Spigot
    compileOnly(libs.minecraft.luckperms)
    compileOnly(libs.minecraft.discordsrv)
    compileOnly(libs.minecraft.essentialsx)
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(libs.minecraft.mockbukkit)
    implementation(projects.modules.core)
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
}
val destination = File("/Users/romanmakeev/Desktop/Server/SMP/smp/plugins")
    .takeIf(File::exists)
    ?: File(rootDir, "jars")

setupSpigotShadow(destination)
setupSpigotProcessor()
