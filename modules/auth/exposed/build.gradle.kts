plugins {
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.klibs.rules.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

linter {
    unit(ru.astrainteractive.klibs.rules.core.unit.model.UnitRole.Technology())
    rootPackage.set("ru.astrainteractive.aspekt.module.auth.exposed")
}

dependencies {
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.jdbc)
    implementation(libs.klibs.mikro.core)
    implementation(libs.klibs.mikro.extensions)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.minecraft.astralibs.core)

    implementation(projects.modules.auth.dao)

    testImplementation(libs.driver.h2)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.tests.kotlin.test)
}
