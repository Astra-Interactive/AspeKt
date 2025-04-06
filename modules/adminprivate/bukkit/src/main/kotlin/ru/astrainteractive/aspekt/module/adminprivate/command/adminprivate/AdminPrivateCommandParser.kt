package ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.module.adminprivate.util.claimChunk
import ru.astrainteractive.aspekt.module.adminprivate.util.claimPlayer
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.BooleanArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.parser.CommandParser

internal class AdminPrivateCommandParser : CommandParser<AdminPrivateCommand.Model, BukkitCommandContext> {

    override fun parse(commandContext: BukkitCommandContext): AdminPrivateCommand.Model {
        val sender = commandContext.sender
        val args = commandContext.args
        commandContext.requirePermission(PluginPermission.AdminClaim)
        return when (args.getOrNull(0)) {
            "map" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                AdminPrivateCommand.Model.ShowMap(player.claimPlayer, player.chunk.claimChunk)
            }

            "claim" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                AdminPrivateCommand.Model.Claim(player.claimPlayer, player.chunk.claimChunk)
            }

            "unclaim" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                AdminPrivateCommand.Model.UnClaim(player.claimPlayer, player.chunk.claimChunk)
            }

            "flag" -> {
                val player = sender as? Player ?: throw AdminPrivateCommand.Error.NotPlayer
                val flag = commandContext.requireArgument(
                    index = 1,
                    type = ArgumentType.Lambda("ChunkFlag", ChunkFlag::valueOf)
                )
                val value = commandContext.requireArgument(2, BooleanArgumentType)
                AdminPrivateCommand.Model.SetFlag(
                    claimPlayer = player.claimPlayer,
                    flag = flag,
                    value = value,
                    chunk = player.chunk.claimChunk
                )
            }

            else -> throw BadArgumentException(args.getOrNull(0), StringArgumentType)
        }
    }
}
