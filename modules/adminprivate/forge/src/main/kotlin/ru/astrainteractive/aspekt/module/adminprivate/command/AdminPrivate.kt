package ru.astrainteractive.aspekt.module.adminprivate.command

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommand
import ru.astrainteractive.aspekt.module.adminprivate.command.adminprivate.AdminPrivateCommandExecutor
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.module.adminprivate.util.getClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.util.toClaimPlayer
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgument
import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgumentType

enum class SecondArgument(override val value: String) : EnumArgument {
    CLAIM("claim"), UNCLAIM("unclaim"), MAP("map"), FLAG("flag")
}

internal fun RegisterCommandsEvent.adminPrivate(
    adminPrivateCommandExecutor: AdminPrivateCommandExecutor
) {
    command("adminprivate") {
        argument(
            alias = "first_arg",
            type = StringArgumentType.string(),
            suggests = SecondArgument.entries.map(SecondArgument::value),
            execute = { ctx ->
                val arg = ctx.requireArgument("first_arg", EnumArgumentType(SecondArgument.entries))
                when (arg) {
                    SecondArgument.CLAIM -> {
                        val player = ctx.source.player ?: return@argument
                        adminPrivateCommandExecutor.execute(
                            AdminPrivateCommand.Model.Claim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = player.getClaimChunk()
                            )
                        )
                    }

                    SecondArgument.UNCLAIM -> {
                        val player = ctx.source.player ?: return@argument
                        adminPrivateCommandExecutor.execute(
                            AdminPrivateCommand.Model.UnClaim(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = player.getClaimChunk()
                            )
                        )
                    }

                    SecondArgument.MAP -> {
                        val player = ctx.source.player ?: return@argument
                        adminPrivateCommandExecutor.execute(
                            AdminPrivateCommand.Model.ShowMap(
                                claimPlayer = player.toClaimPlayer(),
                                chunk = player.getClaimChunk()
                            )
                        )
                    }

                    SecondArgument.FLAG -> Unit
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
                                ctx.requireArgument("first_arg", EnumArgumentType(SecondArgument.entries))
                                val flag = ctx
                                    .getArgument("specific_flag", String::class.java)
                                    .let { flag -> ChunkFlag.entries.firstOrNull { entry -> entry.name == flag } }
                                    ?: return@execute
                                val value = ctx
                                    .getArgument("bool_value", Boolean::class.java)
                                val player = ctx.source.player ?: return@execute
                                adminPrivateCommandExecutor.execute(
                                    AdminPrivateCommand.Model.SetFlag(
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
