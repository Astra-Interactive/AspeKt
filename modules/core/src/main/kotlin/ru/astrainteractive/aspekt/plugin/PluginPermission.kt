package ru.astrainteractive.aspekt.plugin

import ru.astrainteractive.astralibs.permission.Permission

sealed class PluginPermission(override val value: String) : Permission {
    data object Reload : PluginPermission("aspekt.reload")
    data object TellChat : PluginPermission("aspekt.tellchat")
    data object MaxOnline : PluginPermission("aspekt.maxonline")
    data object AtemFrame : PluginPermission("aspekt.atemframe")
    data object Jail {
        data object JailList : PluginPermission("aspekt.jail.list")
        data object JailCreate : PluginPermission("aspekt.jail.create")
        data object JailDelete : PluginPermission("aspekt.jail.delete")
        data object JailInmate : PluginPermission("aspekt.jail.inmate")
        data object JailFree : PluginPermission("aspekt.jail.free")
    }

    data object Entities : PluginPermission("aspekt.entities")
    data object AdminClaim : PluginPermission("aspekt.admin_claim")
    data object ForcePlayerSwear : PluginPermission("aspekt.set_swear.admin")
    class CustomPermission(value: String) : PluginPermission(value)
    sealed class Economy(value: String) : PluginPermission(value) {
        data object SetBalance : Economy("aspekt.economy.set")
    }
}
