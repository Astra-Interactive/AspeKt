package ru.astrainteractive.aspekt.module.claims.command.claim

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlagArgumentConverter
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.getClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.toClaimPlayer
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.literal
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.requirePlayer
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.server.util.ForgeUtil
import ru.astrainteractive.astralibs.server.util.getOnlinePlayers
import ru.astrainteractive.astralibs.server.util.getPlayerGameProfile
import ru.astrainteractive.astralibs.server.util.toPlain

/**
 * Forge Claim command registrar.
 * Builds Brigadier node for:
 * - /claim flag <ChunkFlag> <bool>
 * - /claim add <player>
 * - /claim remove <player>
 * - /claim map
 * - /claim claim
 * - /claim unclaim
 */
class ClaimCommandRegistrar(
    private val claimCommandExecutor: ClaimCommandExecutor,
    private val claimsRepository: ClaimsRepository
) {

    @Suppress("LongMethod")
    fun createNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("claim") {
            literal(ClaimCommandArgument.FLAG.value) {
                argument("ChunkFlag", StringArgumentType.string()) { chunkFlagArg ->
                    hints { ChunkFlag.entries.map(ChunkFlag::name) }
                    argument("bool", BoolArgumentType.bool()) { boolArg ->
                        hints { listOf("true", "false") }
                        runs { ctx ->
                            val flag = ctx.requireArgument(chunkFlagArg, ChunkFlagArgumentConverter)
                            val value = ctx.requireArgument(boolArg)
                            val player = ctx.requirePlayer()
                            claimCommandExecutor.execute(
                                Claimommand.Model.SetFlag(
                                    claimPlayer = player.toClaimPlayer(),
                                    chunk = player.getClaimChunk(),
                                    value = value,
                                    flag = flag
                                )
                            )
                        }
                    }
                }
            }
            literal(ClaimCommandArgument.ADD_MEMBER.value) {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { ForgeUtil.getOnlinePlayers().map { player -> player.name.toPlain() } }
                    runs { ctx ->
                        val ownerPlayer = ctx.source.player ?: return@runs
                        val memberPlayerName = ctx.requireArgument(playerArg)
                        val memberPlayer = ForgeUtil
                            .getPlayerGameProfile(memberPlayerName)
                            ?: return@runs
                        claimCommandExecutor.execute(
                            Claimommand.Model.AddMember(
                                owner = ownerPlayer.toClaimPlayer(),
                                member = memberPlayer.toClaimPlayer()
                            )
                        )
                    }
                }
            }
            literal(ClaimCommandArgument.REMOVE_MEMBER.value) {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { ctx ->
                        ctx.source?.player?.uuid
                            ?.let(claimsRepository::findKrate)
                            ?.cachedStateFlow
                            ?.value
                            ?.members
                            ?.map(ClaimPlayer::username)
                            .orEmpty()
                    }
                    runs { ctx ->
                        val ownerPlayer = ctx.source.player ?: return@runs
                        val memberPlayerName = ctx.requireArgument(playerArg)
                        val memberPlayer = ForgeUtil
                            .getPlayerGameProfile(memberPlayerName)
                            ?: return@runs
                        claimCommandExecutor.execute(
                            Claimommand.Model.RemoveMember(
                                owner = ownerPlayer.toClaimPlayer(),
                                member = memberPlayer.toClaimPlayer()
                            )
                        )
                    }
                }
            }
            literal(ClaimCommandArgument.MAP.value) {
                runs { ctx ->
                    val player = ctx.source.player ?: return@runs
                    claimCommandExecutor.execute(
                        Claimommand.Model.ShowMap(
                            claimPlayer = player.toClaimPlayer(),
                            chunk = player.getClaimChunk()
                        )
                    )
                }
            }
            literal(ClaimCommandArgument.CLAIM.value) {
                runs { ctx ->
                    val player = ctx.source.player ?: return@runs
                    claimCommandExecutor.execute(
                        Claimommand.Model.Claim(
                            claimPlayer = player.toClaimPlayer(),
                            chunk = player.getClaimChunk()
                        )
                    )
                }
            }
            literal(ClaimCommandArgument.UNCLAIM.value) {
                runs { ctx ->
                    val player = ctx.source.player ?: return@runs
                    claimCommandExecutor.execute(
                        Claimommand.Model.UnClaim(
                            claimPlayer = player.toClaimPlayer(),
                            chunk = player.getClaimChunk()
                        )
                    )
                }
            }
        }
    }
}
