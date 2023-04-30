group = libs.versions.plugin.group.get()
version = libs.versions.plugin.version.get()
description = libs.versions.plugin.description.get()

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
