package ru.astrainteractive.aspekt.module.claims.api.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.core.plugin.model.PluginPermission
import ru.astrainteractive.aspekt.module.claims.api.api.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.api.api.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlagArgumentConverter
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimCommand
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.api.util.toClaimPlayer
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KPlayerKCommandSender
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class ClaimCommandRegistrar(
    private val claimCommandExecutor: ClaimCommandExecutor,
    private val claimsRepository: ClaimsRepository,
    private val platformServer: PlatformServer,
    private val multiplatformCommand: MultiplatformCommand,
    private val chunkProvider: ChunkProvider,
    private val commandExceptionHandler: CommandExceptionHandler
) {

    @Suppress("LongMethod")
    private fun createNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("claim") {
                literal(ClaimCommandArgument.FLAG.value) {
                    argument(
                        "ChunkFlag",
                        StringArgumentType.string()
                    ) { chunkFlagArg ->
                        hints { ChunkFlag.entries.map(ChunkFlag::name) }
                        argument(
                            "bool",
                            BoolArgumentType.bool()
                        ) { boolArg ->
                            hints { listOf("true", "false") }
                            runs(commandExceptionHandler::onException) { ctx ->
                                ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                                val flag = ctx.requireArgument(chunkFlagArg, ChunkFlagArgumentConverter)
                                val value = ctx.requireArgument(boolArg)
                                val player = ctx.requirePlayer()
                                claimCommandExecutor.execute(
                                    ClaimCommand.SetFlag(
                                        claimPlayer = player.toClaimPlayer(),
                                        chunk = chunkProvider.findChunk(player),
                                        value = value,
                                        flag = flag
                                    )
                                )
                            }
                        }
                    }
                }
                literal(ClaimCommandArgument.ADD_MEMBER.value) {
                    argument(
                        "player",
                        StringArgumentType.string()
                    ) { playerArg ->
                        hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                        runs(commandExceptionHandler::onException) { ctx ->
                            ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                            val ownerPlayer = ctx.requirePlayer()
                            val memberPlayerName = ctx.requireArgument(playerArg)
                            val memberPlayer = platformServer.findOfflinePlayer(memberPlayerName)
                                ?: return@runs
                            claimCommandExecutor.execute(
                                ClaimCommand.AddMember(
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
                    argument(
                        "player",
                        StringArgumentType.string()
                    ) { playerArg ->
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
                        runs(commandExceptionHandler::onException) { ctx ->
                            ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                            val ownerPlayer = ctx.requirePlayer()
                            val memberPlayerName = ctx.requireArgument(playerArg)
                            val memberPlayer = platformServer.findOfflinePlayer(memberPlayerName)
                                ?: return@runs
                            claimCommandExecutor.execute(
                                ClaimCommand.RemoveMember(
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
                    runs(commandExceptionHandler::onException) { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val player = ctx.requirePlayer()
                        claimCommandExecutor.execute(
                            ClaimCommand.ShowMap(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = chunkProvider.findChunk(player)
                            )
                        )
                    }
                }
                literal(ClaimCommandArgument.CLAIM.value) {
                    runs(commandExceptionHandler::onException) { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val player = ctx.requirePlayer()
                        claimCommandExecutor.execute(
                            ClaimCommand.Claim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = chunkProvider.findChunk(player)
                            )
                        )
                    }
                }
                literal(ClaimCommandArgument.UNCLAIM.value) {
                    runs(commandExceptionHandler::onException) { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val player = ctx.requirePlayer()
                        claimCommandExecutor.execute(
                            ClaimCommand.UnClaim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = chunkProvider.findChunk(player)
                            )
                        )
                    }
                }
            }
        }
    }

    fun createNodes(): List<LiteralArgumentBuilder<Any>> {
        return listOf(createNode())
    }
}
