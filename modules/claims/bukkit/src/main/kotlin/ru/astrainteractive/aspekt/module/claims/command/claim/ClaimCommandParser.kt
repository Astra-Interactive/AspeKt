package ru.astrainteractive.aspekt.module.claims.command.claim

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.util.claimChunk
import ru.astrainteractive.aspekt.module.claims.util.claimPlayer
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.BooleanArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.parser.CommandParser

internal class ClaimCommandParser : CommandParser<Claimommand.Model, BukkitCommandContext> {

    override fun parse(commandContext: BukkitCommandContext): Claimommand.Model {
        val sender = commandContext.sender
        val args = commandContext.args
        commandContext.requirePermission(PluginPermission.AdminClaim)
        return when (args.getOrNull(0)) {
            "map" -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                Claimommand.Model.ShowMap(player.claimPlayer, player.chunk.claimChunk)
            }

            "claim" -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                Claimommand.Model.Claim(player.claimPlayer, player.chunk.claimChunk)
            }

            "unclaim" -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                Claimommand.Model.UnClaim(player.claimPlayer, player.chunk.claimChunk)
            }

            "flag" -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                val flag = commandContext.requireArgument(
                    index = 1,
                    type = ArgumentType.Lambda("ChunkFlag", ChunkFlag::valueOf)
                )
                val value = commandContext.requireArgument(2, BooleanArgumentType)
                Claimommand.Model.SetFlag(
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
