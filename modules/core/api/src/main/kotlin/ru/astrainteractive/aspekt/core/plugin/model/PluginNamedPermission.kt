package ru.astrainteractive.aspekt.core.plugin.model

import ru.astrainteractive.astralibs.server.permission.Permission

data class PluginNamedPermission(override val value: String) : Permission
