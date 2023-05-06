package ru.astrainteractive.buildlogic

import libs
import org.gradle.api.Project

object ProjectConfig {
    val Project.info: ProjectInfo
        get() = DefaultProjectInfo(
            id = libs.versions.project.name.get().toLowerCase(),
            name = libs.versions.project.name.get(),
            version = libs.versions.project.version.get(),
            url = libs.versions.project.url.get(),
            description = libs.versions.project.description.get(),
            authors = libs.versions.project.authors.get().split(";"),
            main = "${libs.versions.project.group.get()}.${libs.versions.project.name.get()}"
        )
}
