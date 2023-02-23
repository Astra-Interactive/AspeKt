package ru.astrainteractive.astraessentials.plugin

import ru.astrainteractive.astralibs.utils.IPermission

sealed class Permission(override val value: String) : IPermission {
    object Reload : Permission("astra_template.reload")
    object Damage : Permission("astra_template.damage")
    object TellChat : Permission("astra_template.tellchat")
    object AtemFrame : Permission("astra_template.atemframe")

}