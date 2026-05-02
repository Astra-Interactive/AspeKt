import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import ru.astrainteractive.gradleplugin.property.util.requireProjectInfo

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.gradle.shadow)
    alias(libs.plugins.klibs.minecraft.resource.processor)
}

dependencies {
    compileOnly(libs.driver.h2)
    compileOnly(libs.driver.jdbc)
    compileOnly(libs.driver.mysql)
    compileOnly(libs.minecraft.discordsrv)
    compileOnly(libs.minecraft.essentialsx)
    compileOnly(libs.minecraft.luckperms)
    compileOnly(libs.minecraft.paper.api)
    compileOnly(libs.minecraft.vaultapi)
    compileOnly(libs.minecraft.kyori.api)

    implementation(libs.klibs.mikro.core)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.minecraft.astralibs.command)
    implementation(libs.minecraft.astralibs.command.bukkit)
    implementation(libs.minecraft.astralibs.core)
    implementation(libs.minecraft.astralibs.core.bukkit)
    implementation(libs.minecraft.astralibs.menu.bukkit)
    implementation(libs.minecraft.bstats)

    implementation(projects.modules.antiswear)
    implementation(projects.modules.autocrop)
    implementation(projects.modules.broadcast)
    implementation(projects.modules.chatgame)
    implementation(projects.modules.claims.api)
    implementation(projects.modules.claims.bukkit)
    implementation(projects.modules.core.api)
    implementation(projects.modules.core.bukkit)
    implementation(projects.modules.economy)
    implementation(projects.modules.inventorysort)
    implementation(projects.modules.invisibleItemFrames)
    implementation(projects.modules.jail)
    implementation(projects.modules.menu)
    implementation(projects.modules.moneyAdvancements)
    implementation(projects.modules.moneydrop)
    implementation(projects.modules.newbee)
    implementation(projects.modules.restrictions)
    implementation(projects.modules.sit)
    implementation(projects.modules.treecapitator)

    testImplementation(libs.tests.kotlin.test)
}

minecraftProcessResource {
    bukkit(
        customProperties = mapOf(
            "libraries" to listOf(
                libs.driver.h2.get(),
                libs.driver.jdbc.get(),
                libs.driver.mysql.get(),
                libs.driver.mariadb.get()
            ).joinToString("\",\"", "[\"", "\"]")
        )
    )
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")
shadowJar.configure {

    val projectInfo = requireProjectInfo
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)

    minimize {
        exclude(dependency(libs.exposed.jdbc.get()))
        exclude(dependency(libs.exposed.dao.get()))
    }
    archiveVersion.set(projectInfo.versionString)
    archiveBaseName = "${requireProjectInfo.name}-${project.name}"
    destinationDirectory = rootDir.resolve("build")
        .resolve("bukkit")
        .resolve("plugins")
        .takeIf(File::exists)
        ?: File(rootDir, "jars").also(File::mkdirs)

    dependencies {
        // Dependencies
        exclude("mozilla/**")
        exclude("javax/**")
        exclude("it/unimi/dsi/**")
        exclude("ch/qos/logback/**")
        exclude("org/intellij/lang/annotations/**")
        exclude("org/jetbrains/annotations/**")
        exclude("org/slf4j/**")
        exclude("org/apache/xmlgraphics/**")
        exclude("org/apache/batik/**")
        exclude("org/apache/commons/logging/**")
        exclude("com/ibm/icu/**")
        // Root
        exclude("_COROUTINE/**")
        exclude("DebugProbesKt.bin")
        exclude("jetty-dir.css")
        exclude("license/**")
        exclude("licenses/**")
        exclude("**LICENCE**")
        exclude("**LICENSE**")
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
        exclude("META-INF/versions/**")
        exclude(dependency("mysql:mysql-connector-java"))
        exclude(dependency("com.mysql:mysql-connector-j"))
        exclude(dependency("org.xerial:sqlite-jdbc"))
        exclude(dependency("com.mojang:brigadier"))
        exclude(dependency("net.kyori:.*"))
    }
    relocate("org.bstats", projectInfo.group)
    listOf(
        "ch.qos.logback",
        "com.charleskorn.kaml",
        "com.ibm.icu",
        "it.krzeminski.snakeyaml",
        "net.thauvin.erik",
        "okio",
        "org.apache",
        "org.intellij",
        "org.jetbrains.annotations",
        "ru.astrainteractive.klibs",
        "ru.astrainteractive.astralibs",
        "io.github.reactivecircus",
        "co.touchlab.stately",
        "google.protobuf",
    ).forEach { pattern -> relocate(pattern, "${projectInfo.group}.$pattern") }
    listOf(
        "kotlinx",
    ).forEach { pattern ->
        relocate(pattern, "${projectInfo.group}.$pattern") {
            exclude("kotlin/kotlin.kotlin_builtins")
        }
    }
}
