pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.essentialsx.net/snapshots/")
        maven("https://files.minecraftforge.net/maven")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.enginehub.org/repo/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo1.maven.org/maven2/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.playpro.com")
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.essentialsx.net/snapshots/")
        maven("https://repo.essentialsx.net/releases/")
        maven("https://files.minecraftforge.net/maven")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://maven.minecraftforge.net/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo1.maven.org/maven2/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.playpro.com")
        maven("https://jitpack.io")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "AspeKt"

// Spigot
include(":instances:bukkit")
include(":instances:neoforge")
include(":instances:forge")
include(":instances:bungee")
// Modules
include(
    ":modules:core:api",
    ":modules:core:bukkit",
    ":modules:core:neoforge",
    ":modules:core:forge",
)
include(":modules:menu")
include(":modules:broadcast")
include(
    ":modules:claims:api",
    ":modules:claims:bukkit",
    ":modules:claims:neoforge",
    ":modules:claims:forge",
)
include(
    ":modules:sethome:api",
)
include(
    ":modules:tpa:api",
)
include(
    ":modules:rtp:api",
    ":modules:rtp:minecraft",
)
include(":modules:moneydrop")
include(":modules:money-advancements")
include(":modules:playtime-reward")
include(":modules:autocrop")
include(":modules:newbee")
include(":modules:antiswear")
include(":modules:chatgame")
include(":modules:economy")
include(":modules:restrictions")
include(":modules:oregeneration")
include(":modules:sit")
include(":modules:inventorysort")
include(":modules:treecapitator")
include(":modules:jail")
include(":modules:command:api")
include(":modules:command:bukkit")
include(
    ":modules:auth:api",
    ":modules:auth:neoforge",
    ":modules:auth:forge",
)
include(":modules:invisible-item-frames")
