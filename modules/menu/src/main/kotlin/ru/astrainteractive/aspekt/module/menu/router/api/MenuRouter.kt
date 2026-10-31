package ru.astrainteractive.aspekt.module.menu.router.api

import ru.astrainteractive.aspekt.module.menu.model.MenuConfiguration
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

internal interface MenuRouter {
    fun openMenu(player: OnlineKPlayer, menuModel: MenuConfiguration)
}
