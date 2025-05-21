package ru.astrainteractive.aspekt.plugin

import ru.astrainteractive.astralibs.permission.Permission

enum class PluginPermission(override val value: String) : Permission {
    RELOAD("aspekt.reload"),
    TELL_CHAT("aspekt.tellchat"),
    MAX_ONLINE("aspekt.maxonline"),
    ATEM_FRAME("aspekt.atemframe"),
    JAIL_LIST("aspekt.jail.list"),
    JAIL_CREATE("aspekt.jail.create"),
    JAIL_DELETE("aspekt.jail.delete"),
    JAIL_INMATE("aspekt.jail.inmate"),
    JAIL_FREE("aspekt.jail.free"),
    ENTITIES("aspekt.entities"),
    ADMIN_CLAIM("aspekt.admin_claim"),
    FORCE_PLAYER_SWEAR("aspekt.set_swear.admin"),
    SET_BALANCE("aspekt.economy.set")
}

data class PluginNamedPermission(override val value: String) : Permission
