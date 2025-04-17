@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.claims.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getPlayerGameProfile
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandArgument
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.Claimommand
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.util.getClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.toClaimPlayer

@Suppress("LongMethod")
internal fun RegisterCommandsEvent.claim(
    claimCommandExecutor: ClaimCommandExecutor,
) {
    literal("claim") {
        literal(ClaimCommandArgument.FLAG.value) {
            stringArgument(
                alias = "ChunkFlag",
                suggests = { ChunkFlag.entries.map(ChunkFlag::name) },
                builder = {
                    argument(
                        alias = "bool",
                        type = BoolArgumentType.bool(),
                        suggests = listOf("true", "false"),
                        execute = execute@{ ctx ->
                            val flag = ctx
                                .getArgument("ChunkFlag", String::class.java)
                                .let { flag -> ChunkFlag.entries.firstOrNull { entry -> entry.name == flag } }
                                ?: return@execute
                            val value = ctx.getArgument("bool", Boolean::class.java)
                            val player = ctx.source.player ?: return@execute
                            claimCommandExecutor.execute(
                                Claimommand.Model.SetFlag(
                                    claimPlayer = player.toClaimPlayer(),
                                    chunk = player.getClaimChunk(),
                                    value = value,
                                    flag = flag
                                )
                            )
                        }
                    )
                }
            )
        }
        literal(ClaimCommandArgument.ADD_MEMBER.value) {
            argument(
                alias = "player",
                type = StringArgumentType.string(),
                execute = execute@{ ctx ->
                    val ownerPlayer = ctx.source.player ?: return@execute
                    val memberPlayerName = ctx.requireArgument(
                        "player",
                        ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
                    )
                    val memberPlayer = ForgeUtil
                        .getPlayerGameProfile(memberPlayerName)
                        ?: return@execute
                    claimCommandExecutor.execute(
                        Claimommand.Model.AddMember(
                            owner = ownerPlayer.toClaimPlayer(),
                            member = memberPlayer.toClaimPlayer()
                        )
                    )
                }
            )
        }
        literal(ClaimCommandArgument.REMOVE_MEMBER.value) {
            argument(
                alias = "player",
                type = StringArgumentType.string(),
                execute = execute@{ ctx ->
                    val ownerPlayer = ctx.source.player ?: return@execute
                    val memberPlayerName = ctx.requireArgument(
                        "player",
                        ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
                    )
                    val memberPlayer = ForgeUtil
                        .getPlayerGameProfile(memberPlayerName)
                        ?: return@execute
                    claimCommandExecutor.execute(
                        Claimommand.Model.RemoveMember(
                            owner = ownerPlayer.toClaimPlayer(),
                            member = memberPlayer.toClaimPlayer()
                        )
                    )
                }
            )
        }
        literal(
            alias = ClaimCommandArgument.MAP.value,
            execute = execute@{ ctx ->
                val player = ctx.source.player ?: return@execute
                claimCommandExecutor.execute(
                    Claimommand.Model.ShowMap(
                        claimPlayer = player.toClaimPlayer(),
                        chunk = player.getClaimChunk()
                    )
                )
            }
        )
        literal(
            alias = ClaimCommandArgument.CLAIM.value,
            execute = execute@{ ctx ->
                val player = ctx.source.player ?: return@execute
                claimCommandExecutor.execute(
                    Claimommand.Model.Claim(
                        claimPlayer = player.toClaimPlayer(),
                        chunk = player.getClaimChunk()
                    )
                )
            }
        )
        literal(
            alias = ClaimCommandArgument.UNCLAIM.value,
            execute = execute@{ ctx ->
                val player = ctx.source.player ?: return@execute
                claimCommandExecutor.execute(
                    Claimommand.Model.UnClaim(
                        claimPlayer = player.toClaimPlayer(),
                        chunk = player.getClaimChunk()
                    )
                )
            }
        )
    }.run(dispatcher::register)
}
