package ru.astrainteractive.astraessentials.plugin

import ru.astrainteractive.astralibs.utils.Permission

sealed class PluginPermission(override val value: String) : Permission {
    object Reload : PluginPermission("astra_template.reload")
    object Damage : PluginPermission("astra_template.damage")
    object TellChat : PluginPermission("astra_template.tellchat")
    object MaxOnline : PluginPermission("astra_template.maxonline")
    object AtemFrame : PluginPermission("astra_template.atemframe")
    object Entities : PluginPermission("astra_template.entities")

}