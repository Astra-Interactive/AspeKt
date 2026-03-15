import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireJinfo
import ru.astrainteractive.gradleplugin.property.extension.ModelPropertyValueExt.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.neoforgegradle)
    alias(libs.plugins.klibs.minecraft.resource.processor)
    alias(libs.plugins.gradle.shadow)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // Kotlin
    shadow(libs.kotlin.coroutines.core)
    // AstraLibs
    shadow(libs.minecraft.astralibs.core)
    shadow(libs.minecraft.astralibs.core.neoforge)
    shadow(libs.minecraft.astralibs.command)
    shadow(libs.kotlin.serialization.kaml)
    shadow(libs.klibs.mikro.core)
    shadow(libs.klibs.kstorage)
    shadow(libs.driver.h2)
    shadow(libs.driver.jdbc)
    shadow(libs.minecraft.kyori.plain)
    shadow(libs.minecraft.kyori.legacy)
    shadow(libs.minecraft.kyori.gson)
    // Local
    shadow(projects.modules.core.api)
    shadow(projects.modules.core.neoforge)
    shadow(projects.modules.auth.api)
    shadow(projects.modules.auth.neoforge)
    shadow(projects.modules.claims.api)
    shadow(projects.modules.claims.neoforge)
    shadow(projects.modules.sethome.api)
    shadow(projects.modules.sethome.neoforge)
    shadow(projects.modules.tpa.api)
    shadow(projects.modules.tpa.neoforge)
    shadow(projects.modules.rtp.api)
    shadow(projects.modules.rtp.neoforge)
}

val destination = rootDir
    .resolve("build")
    .resolve("forge")
    .resolve("mods")
    .takeIf(File::exists)
    ?: File(rootDir, "jars")

val shadowJar by tasks.getting(ShadowJar::class) {
    mergeServiceFiles()
    dependsOn(tasks.named<ProcessResources>("processResources"))
    configurations = listOf(project.configurations.shadow.get())
    isReproducibleFileOrder = true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier = null as String?
    archiveVersion = requireProjectInfo.versionString
    archiveBaseName = "${requireProjectInfo.name}-${project.name}"
    destinationDirectory = rootDir
        .resolve("build")
        .resolve("neoforge")
        .resolve("mods")
        .takeIf(File::exists)
        ?: File(rootDir, "jars")
    dependencies {
        // Dependencies
        exclude(dependency("org.jetbrains:annotations"))
        // Root
        exclude("_COROUTINE/**")
        exclude("DebugProbesKt.bin")
        exclude("jetty-dir.css")
        exclude("license/**")
        exclude("**LICENCE**")
        exclude("**LICENSE**")
        // Other dependencies
        exclude("club/minnced/opus/**")
        exclude("co/touchlab/stately/**")
        exclude("com/google/**")
        exclude("com/ibm/icu/**")
        exclude("com/sun/**")
        exclude("google/protobuf/**")
        exclude("io/github/**")
        exclude("io/javalin/**")
        exclude("jakarta/servlet/**")
        exclude("javax/annotation/**")
        exclude("javax/servlet/**")
        exclude("natives/**")
        exclude("net/luckperms/**")
        exclude("nl/altindag/**")
        exclude("org/bouncycastle/**")
        exclude("org/checkerframework/**")
        exclude("org/conscrypt/**")
        exclude("org/eclipse/**")
        exclude("tomp2p/opuswrapper/**")
        // META
        exclude("META-INF/**.md")
        exclude("META-INF/**.MD")
        exclude("META-INF/**.txt**")
        exclude("META-INF/**LICENCE**")
        exclude("META-INF/com.android.tools/**")
        exclude("META-INF/gradle-plugins/**")
        exclude("META-INF/imports/**")
        exclude("META-INF/license/**")
        exclude("META-INF/maven/**")
        exclude("META-INF/native-image/**")
        exclude("META-INF/native/**")
        exclude("META-INF/proguard/**")
        exclude("META-INF/rewrite/**")
        exclude("META-INF/versions/**")
    }

    // Be sure to relocate EXACT PACKAGES!!
    // For example, relocate org.some.package instead of org
    // Becuase relocation org will break other non-relocated dependencies such as org.minecraft
    // Don't relocate `org.jetbrains.exposed` and `kotlin`
    listOf(
        "ch.qos.logback",
        "club.minnced.discord",
        "club.minnced.opus",
        "com.arkivanov",
        "com.charleskorn.kaml",
        "com.fasterxml",
        "com.neovisionaries",
        "dev.icerock",
        "gnu.trove",
        "it.krzeminski",
        "javax.xml",
        "kotlinx",
        "net.dv8tion",
        "net.kyori",
        "net.thauvin",
        "okhttp3",
        "okio",
        "org.apache",
        "org.h2",
        "org.sqlite",
        "org.jetbrains.kotlin",
        "org.jetbrains.kotlinx",
        "org.json",
        "org.slf4j",
        "org.telegram",
        "org.w3c.css",
        "org.w3c.dom",
        "org.xml.sax",
        "ru.astrainteractive.astralibs",
        "ru.astrainteractive.klibs",
    ).forEach { pattern -> relocate(pattern, "${requireProjectInfo.group}.shade.$pattern") }
}

java.toolchain.languageVersion = JavaLanguageVersion.of(requireJinfo.jtarget.majorVersion)

dependencies {
    compileOnly(libs.minecraft.neoforgeversion)
}

configurations.runtimeElements {
    setExtendsFrom(emptySet())
}
