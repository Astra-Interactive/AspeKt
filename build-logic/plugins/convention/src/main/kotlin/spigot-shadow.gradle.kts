plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow")
}
tasks.shadowJar {
    dependencies {
        include {
            it.moduleGroup == libs.versions.plugin.group.get() || it.moduleGroup.contains("aspekt") || it.moduleGroup.contains("bstats")
        }
    }
    relocate("org.bstats", "${libs.versions.plugin.group.get()}")
    archiveClassifier.set(null as String?)
    archiveBaseName.set(libs.versions.plugin.name.get())
    destinationDirectory.set(File(libs.versions.destionation.spigot.get()))
}
