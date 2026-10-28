package ru.astrainteractive.aspekt.plugin.model

import ru.astrainteractive.astralibs.server.permission.Permission

data class PluginNamedPermission(override val value: String) : Permission
