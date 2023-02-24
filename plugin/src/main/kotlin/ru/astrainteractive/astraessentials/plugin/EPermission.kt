package ru.astrainteractive.astraessentials.plugin

import ru.astrainteractive.astralibs.utils.Permission

sealed class EPermission(override val value: String) : Permission {
    object Reload : EPermission("astra_template.reload")
    object Damage : EPermission("astra_template.damage")
    object TellChat : EPermission("astra_template.tellchat")
    object MaxOnline : EPermission("astra_template.maxonline")
    object AtemFrame : EPermission("astra_template.atemframe")
    object Entities : EPermission("astra_template.entities")

}