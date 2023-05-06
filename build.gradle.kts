group = libs.versions.project.group.get()
version = libs.versions.project.version.get()
description = libs.versions.project.description.get()

plugins {
    java
    `maven-publish`
    `java-library`
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.gradle.buildconfig) apply false
    alias(libs.plugins.gradle.shadow) apply false
    id("detekt-convention")
}

tasks.create("PublishPrimaryVersion") {
    ru.astrainteractive.buildlogic.PublishPrimaryVersionTask(this).invoke()
}
