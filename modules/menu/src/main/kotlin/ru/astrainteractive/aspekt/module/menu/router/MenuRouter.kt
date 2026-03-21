package ru.astrainteractive.aspekt.module.menu.router

import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

internal interface MenuRouter {
    fun openMenu(player: OnlineKPlayer, menuModel: MenuModel)
}
