plugins {
    id("spigot-resource-processor")
    id("spigot-shadow")
    id("basic-java")
}

dependencies {
    // Kotlin
    compileOnly(libs.bundles.kotlin)
    // Spigot dependencies
    compileOnly(libs.minecraft.paper.api)
    implementation(libs.minecraft.bstats)
    // AstraLibs
    implementation(libs.minecraft.astralibs.ktxcore)
    implementation(libs.minecraft.astralibs.orm)
    implementation(libs.minecraft.astralibs.di)
    implementation(libs.minecraft.astralibs.spigot.gui)
    implementation(libs.minecraft.astralibs.spigot.core)
    // Spigot
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.discordsrv:discordsrv:1.25.0")
    // Test
    testImplementation(platform(libs.tests.junit.bom))
    testImplementation(libs.bundles.testing.libs)
    testImplementation(libs.bundles.testing.kotlin)
    testImplementation(libs.minecraft.mockbukkit)
}
