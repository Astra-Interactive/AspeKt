package ru.astrainteractive.aspekt.module.auth.api.plugin

import ru.astrainteractive.astralibs.server.permission.Permission

sealed class AuthPermission(override val value: String) : Permission {
    data object Unregister : AuthPermission("aspekt.unregister")
}
