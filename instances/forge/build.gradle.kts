import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import ru.astrainteractive.gradleplugin.property.model.Developer
import ru.astrainteractive.gradleplugin.property.util.requireJinfo
import ru.astrainteractive.gradleplugin.property.util.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.forgegradle)
    alias(libs.plugins.gradle.forgerenamer)
    alias(libs.plugins.gradle.shadow)
}

repositories {
    minecraft.mavenizer(this)
    mavenCentral()
    mavenLocal()
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    // Kotlin
    shadow(libs.kotlin.coroutines.core)
    // AstraLibs
    shadow(libs.minecraft.astralibs.core)
    shadow(libs.minecraft.astralibs.core.forge)
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
    shadow(projects.modules.core.forge)
    shadow(projects.modules.auth.api)
    shadow(projects.modules.auth.forge)
    shadow(projects.modules.claims.api)
    shadow(projects.modules.claims.forge)
    shadow(projects.modules.sethome.api)
    shadow(projects.modules.sethome.forge)
    shadow(projects.modules.tpa.api)
    shadow(projects.modules.tpa.forge)
    shadow(projects.modules.rtp.api)
    shadow(projects.modules.rtp.forge)
}

tasks.named<ProcessResources>("processResources") {
    filteringCharset = "UTF-8"
    duplicatesStrategy = DuplicatesStrategy.WARN
    val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
    val resDirs = sourceSets
        .map(SourceSet::getResources)
        .map(SourceDirectorySet::getSrcDirs)
    from(resDirs) {
        include("META-INF/mods.toml")
        expand(
            mapOf(
                "minecraft_version" to libs.versions.minecraft.forgeversion.get().split("-")[0],
                "forge_version" to libs.versions.minecraft.forgeversion.get().split("-")[1],
                "mod_id" to requireProjectInfo.name.lowercase(),
                "mod_name" to requireProjectInfo.name,
                "mod_license" to "mod_license",
                "mod_version" to requireProjectInfo.versionString,
                "mod_authors" to requireProjectInfo.developersList
                    .map(Developer::id)
                    .joinToString(","),
                "mod_description" to requireProjectInfo.description
            )
        )
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    mergeServiceFiles()
    dependsOn(tasks.named<ProcessResources>("processResources"))
    configurations = listOf(project.configurations.shadow.get())
    isReproducibleFileOrder = true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier = null as String?
    archiveVersion = requireProjectInfo.versionString
    archiveBaseName = "${requireProjectInfo.name}-${project.name}"
    destinationDirectory = rootProject.layout.buildDirectory.get()
        .asFile
        .resolve(project.name)
        .resolve("mods")
        .takeIf(File::exists)
        ?: rootDir.resolve("jars")
    dependencies {
        exclude(dependency("org.jetbrains:annotations"))
        exclude("ch/qos/logback/**")
        exclude("com/ibm/icu/**")
        exclude("it/unimi/dsi/**")
        exclude("javax/**")
        exclude("mozilla/**")
        exclude("org/apache/batik/**")
        exclude("org/apache/commons/logging/**")
        exclude("org/apache/xmlgraphics/**")
        exclude("org/intellij/lang/annotations/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/slf4j/**")
        exclude("org/w3c/dom/**")
        // Use kotlin-forge
        exclude("kotlin/**")
        exclude("_COROUTINE/**")
        exclude("DebugProbesKt.bin")
        exclude("jetty-dir.css")
        exclude("license/**")
        exclude("**LICENCE**")
        exclude("**LICENSE**")
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
        exclude("org/apache/batik/**")
        exclude("org/apache/xmlgraphics/**")
        exclude("org/apache/xmlcommons/**")
        exclude("org/eclipse/**")
        exclude("jdk/xml/**")
        exclude("org/w3c/**")
        exclude("tomp2p/opuswrapper/**")
        exclude("org/slf4j/**")
        exclude("javax/xml/**")
        exclude("org/xml/**")
        // META
        exclude("META-INF/**.md")
        exclude("META-INF/**.MD")
        exclude("META-INF/**.txt**")
        exclude("META-INF/**LICENCE**")
        exclude("META-INF/com.android.tools/**")
        exclude("META-INF/gradle-plugins/**")
        exclude("META-INF/imports/**")
        exclude("META-INF/kotlin-reflection.kotlin_module")
        exclude("META-INF/license/**")
        exclude("META-INF/maven/**")
        exclude("META-INF/native-image/**")
        exclude("META-INF/native/**")
        exclude("META-INF/proguard/**")
        exclude("META-INF/rewrite/**")
        exclude("META-INF/services/kotlin.reflect.**")
        // Don't exclude META-INF/versions for forge
    }

    relocate("org.bstats", requireProjectInfo.group)
    buildList {
        add("ch.qos.logback")
        add("club.minnced.discord")
        add("club.minnced.opus")
        add("co.touchlab.stately")
        add("com.arkivanov")
        add("com.charleskorn.kaml")
        add("com.fasterxml")
        add("com.ibm.icu")
        add("com.neovisionaries")
        add("dev.icerock")
        add("gnu.trove")
        add("google.protobuf")
        add("io.github.reactivecircus")
        add("it.krzeminski")
        add("it.krzeminski.snakeyaml")
        add("kotlinx")
        add("net.dv8tion")
        add("net.kyori")
        add("net.thauvin")
        add("okhttp3")
        add("okio")
        add("org.apache")
        add("org.h2")
        add("org.intellij")
        add("org.jetbrains.annotations")
        add("org.jetbrains.kotlinx")
        add("org.json")
        add("org.sqlite")
        add("org.telegram")
        add("org.w3c.css")
        add("org.w3c.dom")
        add("ru.astrainteractive.astralibs")
        add("ru.astrainteractive.klibs")
        add("tomp2p.opuswrapper")
    }.forEach { pattern -> relocate(pattern, "${requireProjectInfo.group}.shade.$pattern") }
}

java.toolchain.languageVersion = JavaLanguageVersion.of(requireJinfo.jtarget.majorVersion)

minecraft {
    mappings("official", "1.20.1")
    useDefaultAccessTransformer()
}

dependencies {
    compileOnly(minecraft.dependency(libs.minecraft.forgeversion.get()))
}

configurations.runtimeElements {
    setExtendsFrom(emptySet())
}

renamer {
    mappings.from(minecraft.dependency.toSrgFile)
}

val reobfShadowJar by renamer.classes(tasks.named<Jar>("shadowJar")) {
    output = input
}

shadowJar.finalizedBy(reobfShadowJar)
reobfShadowJar.mustRunAfter(shadowJar)
