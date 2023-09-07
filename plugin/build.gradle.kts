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
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.klibs.kdi)
    implementation(libs.minecraft.astralibs.spigot.gui)
    implementation(libs.minecraft.astralibs.spigot.core)
    implementation(libs.minecraft.vaultapi)
    // Spigot
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.discordsrv:discordsrv:1.25.0")
    compileOnly("net.essentialsx:EssentialsX:2.20.1")
    // Test
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.tests.kotlin.test)
    testImplementation(libs.minecraft.mockbukkit)
}
val localFile = File("D:\\Minecraft Servers\\Servers\\esmp-configuration\\anarchy\\plugins")
if (localFile.exists()) setupSpigotShadow(localFile) else setupSpigotShadow()
setupSpigotProcessor()
