package ru.astrainteractive.astraessentials.utils

import ru.astrainteractive.astralibs.utils.IPermission

sealed class Permission(override val value: String) : IPermission {
    object Reload : Permission("astra_template.reload")
    object Damage : Permission("astra_template.damage")
}