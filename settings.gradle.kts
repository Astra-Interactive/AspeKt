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
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
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
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "AspeKt"

// Spigot
include(":instances:bukkit")
include(":instances:velocity")
// Modules
include(":modules:core")
include(":modules:menu")
include(":modules:broadcast")
include(":modules:adminprivate")
include(":modules:discordlink")
include(":modules:towny-discord")
include(":modules:moneydrop")
include(":modules:money-advancements")
include(":modules:autocrop")
include(":modules:newbee")
include(":modules:antiswear")
include(":modules:chatgame")
include(":modules:economy")
include(":modules:entities")
include(":modules:restrictions")
include(":modules:sit")
include(":modules:inventorysort")
include(":modules:treecapitator")
include(":modules:jail")
