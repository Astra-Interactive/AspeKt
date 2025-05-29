@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.claims.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandArgument
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.Claimommand
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.getClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.toClaimPlayer
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.literal
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.command.util.stringArgument
import ru.astrainteractive.astralibs.server.util.ForgeUtil
import ru.astrainteractive.astralibs.server.util.getOnlinePlayers
import ru.astrainteractive.astralibs.server.util.getPlayerGameProfile
import ru.astrainteractive.astralibs.server.util.toPlain

@Suppress("LongMethod")
internal fun RegisterCommandsEvent.claim(
    claimCommandExecutor: ClaimCommandExecutor,
    claimsRepository: ClaimsRepository
) {
    command("claim") {
        literal(ClaimCommandArgument.FLAG.value) {
            stringArgument("ChunkFlag") {
                hints(ChunkFlag.entries.map(ChunkFlag::name))
                argument("bool", BoolArgumentType.bool()) {
                    hints(listOf("true", "false"))
                    runs { ctx ->
                        val flag = ctx
                            .getArgument("ChunkFlag", String::class.java)
                            .let { flag -> ChunkFlag.entries.firstOrNull { entry -> entry.name == flag } }
                            ?: return@runs
                        val value = ctx.getArgument("bool", Boolean::class.java)
                        val player = ctx.source.player ?: return@runs
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
            stringArgument("player") {
                hints(ForgeUtil.getOnlinePlayers().map { player -> player.name.toPlain() })
                runs { ctx ->
                    val ownerPlayer = ctx.source.player ?: return@runs
                    val memberPlayerName = ctx.requireArgument(
                        "player",
                        ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
                    )
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
            stringArgument("player") {
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
                    val memberPlayerName = ctx.requireArgument(
                        "player",
                        ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
                    )
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
    }.run(dispatcher::register)
}
