import org.gradle.api.file.DuplicatesStrategy
import ru.astrainteractive.buildlogic.ProjectConfig.info

plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

tasks.processResources {
    filteringCharset = "UTF-8"
    inputs.property("version", libs.versions.plugin.version.get())
    filesMatching("velocity-plugin.json") {
        mutableMapOf(
            "id" to info.id,
            "name" to info.name,
            "version" to info.version,
            "url" to info.url,
            "authors" to info.authors,
            "main" to info.main
        ).also(::expand)
    }
}