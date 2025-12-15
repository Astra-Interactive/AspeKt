package ru.astrainteractive.aspekt.module.tpa.command.tpa

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommand
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.server.util.NeoForgeUtil
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer
import ru.astrainteractive.astralibs.server.util.getOnlinePlayer
import ru.astrainteractive.astralibs.server.util.getOnlinePlayers
import ru.astrainteractive.astralibs.server.util.toPlain

/**
 * Forge TPA commands registrar. Registers:
 * - /tpa <player>
 * - /tpahere <player>
 * - /tpacancel
 * - /tpaccept
 * - /tpadeny
 */
class TpaCommandRegistrar(
    private val executor: TpaCommandExecutor
) {
    private fun createTpaNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("tpa") {
            argument("player", StringArgumentType.string()) { playerArg ->
                hints { NeoForgeUtil.getOnlinePlayers().map { it.name.toPlain() } }
                runs { ctx ->
                    val targetPlayerName = ctx.requireArgument(playerArg)
                    TpaCommand.TpaTo(
                        executorPlayer = ctx.source.player
                            ?.asOnlineMinecraftPlayer()
                            ?: return@runs,
                        targetPlayer = NeoForgeUtil.getOnlinePlayer(targetPlayerName)
                            ?.asOnlineMinecraftPlayer()
                            ?: return@runs
                    ).run(executor::execute)
                }
            }
        }
    }

    private fun createTpaHerNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("tpahere") {
            argument("player", StringArgumentType.string()) { playerArg ->
                hints { NeoForgeUtil.getOnlinePlayers().map { it.name.toPlain() } }
                runs { ctx ->
                    val targetPlayerName = ctx.requireArgument(playerArg)
                    TpaCommand.TpaHere(
                        executorPlayer = ctx.source.player
                            ?.asOnlineMinecraftPlayer()
                            ?: return@runs,
                        targetPlayer = NeoForgeUtil.getOnlinePlayer(targetPlayerName)
                            ?.asOnlineMinecraftPlayer()
                            ?: return@runs
                    ).run(executor::execute)
                }
            }
        }
    }

    private fun createTpaCancelNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("tpacancel") {
            runs { ctx ->
                TpaCommand.TpaCancel(
                    executorPlayer = ctx.source.player
                        ?.asOnlineMinecraftPlayer()
                        ?: return@runs,
                ).run(executor::execute)
            }
        }
    }

    private fun createTpaAcceptNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("tpaccept") {
            runs { ctx ->
                TpaCommand.TpaAccept(
                    executorPlayer = ctx.source.player
                        ?.asOnlineMinecraftPlayer()
                        ?: return@runs,
                ).run(executor::execute)
            }
        }
    }

    private fun createTpaDenyNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("tpadeny") {
            runs { ctx ->
                TpaCommand.TpaDeny(
                    executorPlayer = ctx.source.player
                        ?.asOnlineMinecraftPlayer()
                        ?: return@runs,
                ).run(executor::execute)
            }
        }
    }

    fun createNodes() = listOf(
        createTpaNode(),
        createTpaHerNode(),
        createTpaCancelNode(),
        createTpaAcceptNode(),
        createTpaDenyNode()
    )
}
