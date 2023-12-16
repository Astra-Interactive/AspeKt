package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

internal class AdminPrivateCommandParser : CommandParser<AdminPrivateCommand.Output> {

    override fun parse(args: Array<out String>, sender: CommandSender): AdminPrivateCommand.Output {
        if (!sender.toPermissible().hasPermission(PluginPermission.AdminClaim)) {
            return AdminPrivateCommand.Output.NoPermission
        }
        return when (args.getOrNull(0)) {
            "map" -> {
                (sender as? Player)?.let(AdminPrivateCommand.Output::ShowMap) ?: AdminPrivateCommand.Output.NotPlayer
            }

            "claim" -> {
                (sender as? Player)?.let(AdminPrivateCommand.Output::Claim) ?: AdminPrivateCommand.Output.NotPlayer
            }

            "unclaim" -> {
                (sender as? Player)?.let(AdminPrivateCommand.Output::UnClaim) ?: AdminPrivateCommand.Output.NotPlayer
            }

            "flag" -> {
                val flag = args.getOrNull(1)?.let(ChunkFlag::valueOf)
                flag ?: return AdminPrivateCommand.Output.WrongUsage
                val value = args.getOrNull(2)?.toBoolean()
                value ?: return AdminPrivateCommand.Output.WrongUsage
                val player = (sender as? Player)
                player ?: return AdminPrivateCommand.Output.NotPlayer
                AdminPrivateCommand.Output.SetFlag(
                    player = player,
                    flag = flag,
                    value = value
                )
            }

            else -> AdminPrivateCommand.Output.WrongUsage
        }
    }
}
