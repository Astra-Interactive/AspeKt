package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal interface AdminPrivateCommand {
    sealed interface Model {
        class ShowMap(val player: Player) : Model
        class Claim(val player: Player) : Model
        class UnClaim(val player: Player) : Model
        class SetFlag(
            val player: Player,
            val flag: ChunkFlag,
            val value: Boolean
        ) : Model
    }

    sealed class Error(message: String) : CommandException(message) {
        data object NotPlayer : Error("The sender is not a player")
    }
}
