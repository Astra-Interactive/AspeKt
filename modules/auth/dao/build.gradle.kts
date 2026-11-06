plugins {
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.klibs.rules.gradle.linter)
    id("ru.astrainteractive.gradleplugin.java.version")
}

linter {
    unit(ru.astrainteractive.klibs.rules.core.unit.model.UnitRole.StorageContract())
    rootPackage.set("ru.astrainteractive.aspekt.module.auth.dao")
}

dependencies {
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.tests.kotlin.test)
}
