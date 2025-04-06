package ru.astrainteractive.aspekt.module.auth.api.permission

import ru.astrainteractive.astralibs.permission.Permission

sealed class AuthPermission(override val value: String) : Permission {
    data object Unregister : AuthPermission("aspekt.unregister")
}
