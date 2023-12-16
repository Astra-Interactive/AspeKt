package ru.astrainteractive.aspekt.gui

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.menu.model.MenuModel

interface Router {
    sealed interface Route {
        class Entities(val player: Player) : Route
        class Menu(
            val player: Player,
            val menuModel: MenuModel
        ) : Route
    }

    fun open(route: Router.Route)
}
