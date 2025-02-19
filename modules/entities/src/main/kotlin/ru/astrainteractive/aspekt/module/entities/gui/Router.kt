package ru.astrainteractive.aspekt.module.entities.gui

import org.bukkit.entity.Player

internal interface Router {
    sealed interface Route {
        class Entities(val player: Player) : Route
    }

    fun open(route: Route)
}
