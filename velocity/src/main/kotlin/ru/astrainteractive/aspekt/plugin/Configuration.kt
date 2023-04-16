package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val isLoginRestricted: Boolean = false
)