package ru.astrainteractive.buildlogic

import libs
import org.gradle.api.Project

object ProjectConfig {
    val Project.info: ProjectInfo
        get() = DefaultProjectInfo(
            id = libs.versions.plugin.name.get().toLowerCase(),
            name = libs.versions.plugin.name.get(),
            version = libs.versions.plugin.version.get(),
            url = libs.versions.plugin.url.get(),
            description = libs.versions.plugin.description.get(),
            authors = libs.versions.plugin.authors.get().split(";"),
            main = "${libs.versions.plugin.group.get()}.${libs.versions.plugin.name.get()}"
        )
}