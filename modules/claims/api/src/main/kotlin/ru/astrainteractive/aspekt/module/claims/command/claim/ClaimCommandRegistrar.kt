package ru.astrainteractive.aspekt.module.claims.command.claim

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlagArgumentConverter
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.server.location.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.util.toClaimPlayer
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KPlayerKCommandSender
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.klibs.mikro.core.util.tryCast

/**
 * Platform-agnostic Claim command registrar.
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
    private val claimsRepository: ClaimsRepository,
    private val platformServer: PlatformServer,
    private val multiplatformCommand: MultiplatformCommand,
    private val registrarContext: CommandRegistrarContext,
    private val chunkProvider: ChunkProvider
) {

    @Suppress("LongMethod")
    private fun createNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("claim") {
                literal(ClaimCommandArgument.FLAG.value) {
                    argument("ChunkFlag", StringArgumentType.string()) { chunkFlagArg ->
                        hints { ChunkFlag.entries.map(ChunkFlag::name) }
                        argument("bool", BoolArgumentType.bool()) { boolArg ->
                            hints { listOf("true", "false") }
                            runs { ctx ->
                                ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                                val flag = ctx.requireArgument(chunkFlagArg, ChunkFlagArgumentConverter)
                                val value = ctx.requireArgument(boolArg)
                                val player = ctx.requirePlayer()
                                claimCommandExecutor.execute(
                                    Claimommand.Model.SetFlag(
                                        claimPlayer = player.toClaimPlayer(),
                                        chunk = chunkProvider.getChunk(player),
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
                        hints { platformServer.getOnlinePlayers().map { it.name } }
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                            val ownerPlayer = ctx.requirePlayer()
                            val memberPlayerName = ctx.requireArgument(playerArg)
                            val memberPlayer = platformServer.findOfflinePlayer(memberPlayerName)
                                ?: return@runs
                            claimCommandExecutor.execute(
                                Claimommand.Model.AddMember(
                                    owner = ownerPlayer.toClaimPlayer(),
                                    member = ClaimPlayer(
                                        uuid = memberPlayer.uuid,
                                        username = memberPlayer.name ?: memberPlayerName
                                    )
                                )
                            )
                        }
                    }
                }
                literal(ClaimCommandArgument.REMOVE_MEMBER.value) {
                    argument("player", StringArgumentType.string()) { playerArg ->
                        hints { ctx ->
                            ctx.getSender()
                                .tryCast<KPlayerKCommandSender>()
                                ?.instance
                                ?.uuid
                                ?.let(claimsRepository::findKrate)
                                ?.cachedStateFlow
                                ?.value
                                ?.members
                                ?.map(ClaimPlayer::username)
                                .orEmpty()
                        }
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                            val ownerPlayer = ctx.requirePlayer()
                            val memberPlayerName = ctx.requireArgument(playerArg)
                            val memberPlayer = platformServer.findOfflinePlayer(memberPlayerName)
                                ?: return@runs
                            claimCommandExecutor.execute(
                                Claimommand.Model.RemoveMember(
                                    owner = ownerPlayer.toClaimPlayer(),
                                    member = ClaimPlayer(
                                        uuid = memberPlayer.uuid,
                                        username = memberPlayer.name ?: memberPlayerName
                                    )
                                )
                            )
                        }
                    }
                }
                literal(ClaimCommandArgument.MAP.value) {
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val player = ctx.requirePlayer()
                        claimCommandExecutor.execute(
                            Claimommand.Model.ShowMap(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = chunkProvider.getChunk(player)
                            )
                        )
                    }
                }
                literal(ClaimCommandArgument.CLAIM.value) {
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val player = ctx.requirePlayer()
                        claimCommandExecutor.execute(
                            Claimommand.Model.Claim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = chunkProvider.getChunk(player)
                            )
                        )
                    }
                }
                literal(ClaimCommandArgument.UNCLAIM.value) {
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val player = ctx.requirePlayer()
                        claimCommandExecutor.execute(
                            Claimommand.Model.UnClaim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = chunkProvider.getChunk(player)
                            )
                        )
                    }
                }
            }
        }
    }

    fun register() {
        registrarContext.registerWhenReady(createNode())
    }
}
