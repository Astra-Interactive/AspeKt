package ru.astrainteractive.aspekt.module.economy.command.ekon.argument

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType
import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal object OfflinePlayerArgument : ArgumentType<OfflinePlayer> {
    override val key: String = "OfflinePlayerArgument"

    override fun transform(value: String): OfflinePlayer {
        val offlinePlayer = Bukkit.getOfflinePlayer(value)
        if (offlinePlayer.name == null) throw PlayerNotFound(value)
        return offlinePlayer
    }

    data class PlayerNotFound(val name: String) : CommandException("Player not found: $name")
}
