plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("ru.astrainteractive.gradleplugin.detekt")
    id("ru.astrainteractive.gradleplugin.java.version")
}

dependencies {
    api(libs.minecraft.astralibs.core)

    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.serialization.json)

    testImplementation(libs.tests.kotlin.test)
}
