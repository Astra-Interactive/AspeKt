package ru.astrainteractive.aspekt.module.claims.command.claim

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.util.asClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.asClaimPlayer
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.argumenttype.OnlinePlayerArgumentConverter
import ru.astrainteractive.astralibs.command.api.util.argument
import ru.astrainteractive.astralibs.command.api.util.command
import ru.astrainteractive.astralibs.command.api.util.hints
import ru.astrainteractive.astralibs.command.api.util.literal
import ru.astrainteractive.astralibs.command.api.util.requireArgument
import ru.astrainteractive.astralibs.command.api.util.requirePermission
import ru.astrainteractive.astralibs.command.api.util.requirePlayer
import ru.astrainteractive.astralibs.command.api.util.runs
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

/**
 * /claim flag <ChunkFlag> <bool>
 * /claim add <player>
 * /claim remove <player>
 * /claim map
 * /claim claim
 * /claim unclaim
 */
internal class ClaimCommandRegistrar(
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val executor: ClaimCommandExecutor,
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    fun createNode(): LiteralCommandNode<CommandSourceStack> {
        return command("claim") {
            // /claim flag <ChunkFlag> <bool>
            literal(ClaimCommandArgument.FLAG.value) {
                argument("ChunkFlag", StringArgumentType.string()) { flagArg ->
                    hints { ChunkFlag.entries.map(ChunkFlag::name) }
                    argument("bool", BoolArgumentType.bool()) { boolArg ->
                        hints { listOf("true", "false") }
                        runs { ctx ->
                            ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                            val player: Player = ctx.requirePlayer()
                            val flagName = ctx.requireArgument(flagArg)
                            val flag = ChunkFlag.entries.firstOrNull { it.name.equals(flagName, true) }
                            if (flag == null) {
                                ctx.source.sender.sendMessage(kyori.toComponent(translation.general.wrongUsage))
                                return@runs
                            }
                            val value = ctx.requireArgument(boolArg)
                            executor.execute(
                                Claimommand.Model.SetFlag(
                                    claimPlayer = player.asClaimPlayer(),
                                    chunk = player.chunk.asClaimChunk(),
                                    value = value,
                                    flag = flag
                                )
                            )
                        }
                    }
                }
            }
            // /claim add <player>
            literal(ClaimCommandArgument.ADD_MEMBER.value) {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val owner: Player = ctx.requirePlayer()
                        val member: Player = ctx.requireArgument(playerArg, OnlinePlayerArgumentConverter)
                        executor.execute(
                            Claimommand.Model.AddMember(
                                owner = owner.asClaimPlayer(),
                                member = member.asClaimPlayer()
                            )
                        )
                    }
                }
            }
            // /claim remove <player>
            literal(ClaimCommandArgument.REMOVE_MEMBER.value) {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { Bukkit.getOnlinePlayers().map(Player::getName) }
                    runs { ctx ->
                        ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                        val owner: Player = ctx.requirePlayer()
                        val member: Player = ctx.requireArgument(playerArg, OnlinePlayerArgumentConverter)
                        executor.execute(
                            Claimommand.Model.RemoveMember(
                                owner = owner.asClaimPlayer(),
                                member = member.asClaimPlayer()
                            )
                        )
                    }
                }
            }
            // /claim map
            literal(ClaimCommandArgument.MAP.value) {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                    val player: Player = ctx.requirePlayer()
                    executor.execute(
                        Claimommand.Model.ShowMap(
                            claimPlayer = player.asClaimPlayer(),
                            chunk = player.chunk.asClaimChunk()
                        )
                    )
                }
            }
            // /claim claim
            literal(ClaimCommandArgument.CLAIM.value) {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                    val player: Player = ctx.requirePlayer()
                    executor.execute(
                        Claimommand.Model.Claim(
                            claimPlayer = player.asClaimPlayer(),
                            chunk = player.chunk.asClaimChunk()
                        )
                    )
                }
            }
            // /claim unclaim
            literal(ClaimCommandArgument.UNCLAIM.value) {
                runs { ctx ->
                    ctx.requirePermission(PluginPermission.ADMIN_CLAIM)
                    val player: Player = ctx.requirePlayer()
                    executor.execute(
                        Claimommand.Model.UnClaim(
                            claimPlayer = player.asClaimPlayer(),
                            chunk = player.chunk.asClaimChunk()
                        )
                    )
                }
            }
        }.build()
    }
}
