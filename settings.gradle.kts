pluginManagement {
    repositories {
        maven("https://plugins.gradle.org/m2/")
        maven("https://jitpack.io")
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        google()
    }
}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        maven("https://mvn.lumine.io/repository/maven-public/") { metadataSources { artifact() } }
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.glaremasters.me/repository/towny/")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.essentialsx.net/snapshots/")
        maven("https://repo.essentialsx.net/releases/")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://m2.dv8tion.net/releases")
        maven("https://repo1.maven.org/maven2/")
        maven("https://maven.playpro.com")
        maven("https://jitpack.io")
    }
    versionCatalogs { create("klibs") { from(files("./gradle/klibs.versions.toml")) } }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "AspeKt"

// Spigot
include("plugin")
include("velocity")
// Modules
include(":modules:core")
include(":modules:menu")
include(":modules:broadcast")
include(":modules:adminprivate")
include(":modules:discordlink")
include(":modules:towny-discord")
include(":modules:moneydrop")
include(":modules:autocrop")
