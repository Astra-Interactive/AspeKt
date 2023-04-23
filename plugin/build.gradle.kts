plugins {
    id("spigot-resource-processor")
    id("spigot-shadow")
    id("basic-java")
}

dependencies {

    // Kotlin
    compileOnly(libs.bundles.kotlin)
    // Spigot dependencies
    compileOnly(libs.bundles.minecraft.bukkit)
    // AstraLibs
    compileOnly(libs.astralibs.ktxCore)
    compileOnly(libs.astralibs.orm)
    compileOnly(libs.astralibs.spigotGui)
    compileOnly(libs.astralibs.spigotCore)
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.discordsrv:discordsrv:1.25.0")
    // Tests
    testImplementation(platform(libs.tests.junit.bom))
    testImplementation(libs.bundles.testing.libs)
}