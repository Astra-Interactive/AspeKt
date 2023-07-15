package ru.astrainteractive.aspekt.plugin

import ru.astrainteractive.astralibs.utils.Permission

sealed class PluginPermission(override val value: String) : Permission {
    data object Reload : PluginPermission("aspekt.reload")
    data object TellChat : PluginPermission("aspekt.tellchat")
    data object MaxOnline : PluginPermission("aspekt.maxonline")
    data object AtemFrame : PluginPermission("aspekt.atemframe")
    data object Entities : PluginPermission("aspekt.entities")
    data object AdminClaim : PluginPermission("aspekt.admin_claim")
}
