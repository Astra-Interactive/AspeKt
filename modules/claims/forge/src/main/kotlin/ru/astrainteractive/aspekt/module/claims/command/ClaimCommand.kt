@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.claims.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandArgument
import ru.astrainteractive.aspekt.module.claims.command.claim.ClaimCommandExecutor
import ru.astrainteractive.aspekt.module.claims.command.claim.Claimommand
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.util.getClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.toClaimPlayer
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgumentType

@Suppress("LongMethod")
internal fun RegisterCommandsEvent.claim(
    claimCommandExecutor: ClaimCommandExecutor
) {
    command("claim") {
        argument(
            alias = "first_arg",
            type = StringArgumentType.string(),
            suggests = ClaimCommandArgument.entries.map(ClaimCommandArgument::value),
            execute = { ctx ->
                val arg = ctx.requireArgument("first_arg", EnumArgumentType(ClaimCommandArgument.entries))
                when (arg) {
                    ClaimCommandArgument.CLAIM -> {
                        val player = ctx.source.player ?: return@argument
                        claimCommandExecutor.execute(
                            Claimommand.Model.Claim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = player.getClaimChunk()
                            )
                        )
                    }

                    ClaimCommandArgument.UNCLAIM -> {
                        val player = ctx.source.player ?: return@argument
                        claimCommandExecutor.execute(
                            Claimommand.Model.UnClaim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = player.getClaimChunk()
                            )
                        )
                    }

                    ClaimCommandArgument.MAP -> {
                        val player = ctx.source.player ?: return@argument
                        claimCommandExecutor.execute(
                            Claimommand.Model.ShowMap(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = player.getClaimChunk()
                            )
                        )
                    }

                    ClaimCommandArgument.FLAG -> Unit
                    ClaimCommandArgument.ADD_MEMBER -> TODO()
                    ClaimCommandArgument.REMOVE_MEMBER -> TODO()
                }
            },
            builder = {
                argument(
                    alias = "specific_flag",
                    suggests = ChunkFlag.entries.map(ChunkFlag::name),
                    type = StringArgumentType.string(),
                    builder = {
                        argument(
                            alias = "bool_value",
                            suggests = listOf("true", "false"),
                            type = BoolArgumentType.bool(),
                            execute = execute@{ ctx ->
                                ctx.requireArgument("first_arg", EnumArgumentType(ClaimCommandArgument.entries))
                                val flag = ctx
                                    .getArgument("specific_flag", String::class.java)
                                    .let { flag -> ChunkFlag.entries.firstOrNull { entry -> entry.name == flag } }
                                    ?: return@execute
                                val value = ctx
                                    .getArgument("bool_value", Boolean::class.java)
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
        )
    }
}
