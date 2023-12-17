package ru.astrainteractive.aspekt.module.menu.router

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.menu.model.MenuModel

interface MenuRouter {
    fun openMenu(player: Player, menuModel: MenuModel)
}
