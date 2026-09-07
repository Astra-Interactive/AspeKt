package ru.astrainteractive.aspekt.module.sethome.plugin

import ru.astrainteractive.astralibs.server.permission.Permission

internal sealed class SetHomePermission(override val value: String) : Permission {
    data object SetHome : SetHomePermission("aspekt.sethome")
    data object DelHome : SetHomePermission("aspekt.delhome")
    data object Home : SetHomePermission("aspekt.home")
}
