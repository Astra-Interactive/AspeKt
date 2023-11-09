package ru.astrainteractive.aspekt.command.adminprivate

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

class AdminPrivateCommandParser : CommandParser<AdminPrivateCommandParser.Output> {
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

    override val alias: String = "adminprivate"

    override fun parse(args: Array<out String>, sender: CommandSender): Output {
        if (!sender.toPermissible().hasPermission(PluginPermission.AdminClaim)) {
            return Output.NoPermission
        }
        return when (args.getOrNull(0)) {
            "map" -> {
                (sender as? Player)?.let(Output::ShowMap) ?: Output.NotPlayer
            }

            "claim" -> {
                (sender as? Player)?.let(Output::Claim) ?: Output.NotPlayer
            }

            "unclaim" -> {
                (sender as? Player)?.let(Output::UnClaim) ?: Output.NotPlayer
            }

            "flag" -> {
                val flag = args.getOrNull(1)?.let(ChunkFlag::valueOf)
                flag ?: return Output.WrongUsage
                val value = args.getOrNull(2)?.toBoolean()
                value ?: return Output.WrongUsage
                val player = (sender as? Player)
                player ?: return Output.NotPlayer
                Output.SetFlag(
                    player = player,
                    flag = flag,
                    value = value
                )
            }

            else -> Output.WrongUsage
        }
    }
}
