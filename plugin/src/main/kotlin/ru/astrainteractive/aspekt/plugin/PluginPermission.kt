package ru.astrainteractive.aspekt.plugin

import ru.astrainteractive.astralibs.utils.Permission

sealed class PluginPermission(override val value: String) : Permission {
    object Reload : PluginPermission("aspekt.reload")
    object TellChat : PluginPermission("aspekt.tellchat")
    object MaxOnline : PluginPermission("aspekt.maxonline")
    object AtemFrame : PluginPermission("aspekt.atemframe")
    object Entities : PluginPermission("aspekt.entities")

}