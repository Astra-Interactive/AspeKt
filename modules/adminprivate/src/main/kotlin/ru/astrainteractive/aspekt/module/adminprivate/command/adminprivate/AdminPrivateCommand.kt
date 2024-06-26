package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.astralibs.command.api.command.BukkitCommand

internal interface AdminPrivateCommand : BukkitCommand {
    sealed interface Input {
        class ShowMap(val player: Player) : Input
        class Claim(val player: Player) : Input
        class UnClaim(val player: Player) : Input
        class SetFlag(
            val player: Player,
            val flag: ChunkFlag,
            val value: Boolean
        ) : Input
    }

    sealed interface Output {
        data object WrongUsage : Output
        data object NotPlayer : Output
        data object NoPermission : Output
        class ShowMap(val player: Player) : Output
        class Claim(val player: Player) : Output
        class UnClaim(val player: Player) : Output
        class SetFlag(
            val player: Player,
            val flag: ChunkFlag,
            val value: Boolean
        ) : Output
    }
}
