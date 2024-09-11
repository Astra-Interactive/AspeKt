package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.PrimitiveArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.exception.DefaultCommandException
import ru.astrainteractive.astralibs.command.api.parser.CommandParser

internal class AdminPrivateCommandParser : CommandParser<AdminPrivateCommand.Model, BukkitCommandContext> {

    override fun parse(commandContext: BukkitCommandContext): AdminPrivateCommand.Model {
        val sender = commandContext.sender
        val args = commandContext.args
        commandContext.requirePermission(PluginPermission.AdminClaim)
        return when (args.getOrNull(0)) {
            "map" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                player.let(AdminPrivateCommand.Model::ShowMap)
            }

            "claim" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                player.let(AdminPrivateCommand.Model::Claim)
            }

            "unclaim" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                player.let(AdminPrivateCommand.Model::UnClaim)
            }

            "flag" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                val flag = commandContext.requireArgument(
                    index = 1,
                    type = ArgumentType.Lambda("ChunkFlag", ChunkFlag::valueOf)
                )
                val value = commandContext.requireArgument(2, PrimitiveArgumentType.Boolean)
                AdminPrivateCommand.Model.SetFlag(
                    player = player,
                    flag = flag,
                    value = value
                )
            }

            else -> throw DefaultCommandException.BadArgumentException(args.getOrNull(0), PrimitiveArgumentType.String)
        }
    }
}
