package ru.astrainteractive.aspekt.module.claims.command.claim

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.util.asClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.asClaimPlayer
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.BooleanArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgumentType
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.parser.CommandParser

internal class ClaimCommandParser : CommandParser<Claimommand.Model, BukkitCommandContext> {

    override fun parse(commandContext: BukkitCommandContext): Claimommand.Model {
        val sender = commandContext.sender
        val claimArgument = commandContext.requireArgument(0, EnumArgumentType(ClaimCommandArgument.entries))
        commandContext.requirePermission(PluginPermission.ADMIN_CLAIM)
        return when (claimArgument) {
            ClaimCommandArgument.MAP -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                Claimommand.Model.ShowMap(player.asClaimPlayer(), player.chunk.asClaimChunk())
            }

            ClaimCommandArgument.CLAIM -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                Claimommand.Model.Claim(player.asClaimPlayer(), player.chunk.asClaimChunk())
            }

            ClaimCommandArgument.UNCLAIM -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                Claimommand.Model.UnClaim(player.asClaimPlayer(), player.chunk.asClaimChunk())
            }

            ClaimCommandArgument.ADD_MEMBER -> {
                val ownerPlayer = sender as? Player ?: throw Claimommand.Error.NotPlayer
                val memberPlayer = commandContext.requireArgument(1, OnlinePlayerArgument)

                Claimommand.Model.AddMember(
                    owner = ownerPlayer.asClaimPlayer(),
                    member = memberPlayer.asClaimPlayer()
                )
            }

            ClaimCommandArgument.REMOVE_MEMBER -> {
                val ownerPlayer = sender as? Player ?: throw Claimommand.Error.NotPlayer
                val memberPlayer = commandContext.requireArgument(1, OnlinePlayerArgument)

                Claimommand.Model.RemoveMember(
                    owner = ownerPlayer.asClaimPlayer(),
                    member = memberPlayer.asClaimPlayer()
                )
            }

            ClaimCommandArgument.FLAG -> {
                val player = sender as? Player ?: throw Claimommand.Error.NotPlayer
                val flag = commandContext.requireArgument(
                    index = 1,
                    type = ArgumentType.Lambda("ChunkFlag", ChunkFlag::valueOf)
                )
                val value = commandContext.requireArgument(2, BooleanArgumentType)
                Claimommand.Model.SetFlag(
                    claimPlayer = player.asClaimPlayer(),
                    flag = flag,
                    value = value,
                    chunk = player.chunk.asClaimChunk()
                )
            }
        }
    }
}
