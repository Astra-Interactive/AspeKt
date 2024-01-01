package ru.astrainteractive.aspekt.gui

import org.bukkit.entity.Player

interface Router {
    sealed interface Route {
        class Entities(val player: Player) : Route
    }

    fun open(route: Router.Route)
}
