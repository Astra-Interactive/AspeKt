package ru.astrainteractive.aspekt.module.tpa.command.tpa

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommand
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
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
    private val executor: TpaCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand
) {
    private fun createTpaNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpa") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { NeoForgeUtil.getOnlinePlayers().map { it.name.toPlain() } }
                    runs { ctx ->
                        val targetPlayerName = ctx.requireArgument(playerArg)
                        TpaCommand.TpaTo(
                            executorPlayer = ctx.requirePlayer(),
                            targetPlayer = NeoForgeUtil.getOnlinePlayer(targetPlayerName)
                                ?.asOnlineMinecraftPlayer()
                                ?: return@runs
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    private fun createTpaHerNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpahere") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { NeoForgeUtil.getOnlinePlayers().map { it.name.toPlain() } }
                    runs { ctx ->
                        val targetPlayerName = ctx.requireArgument(playerArg)
                        TpaCommand.TpaHere(
                            executorPlayer = ctx.requirePlayer(),
                            targetPlayer = NeoForgeUtil.getOnlinePlayer(targetPlayerName)
                                ?.asOnlineMinecraftPlayer()
                                ?: return@runs
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    private fun createTpaCancelNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpacancel") {
                runs { ctx ->
                    TpaCommand.TpaCancel(
                        executorPlayer = ctx.requirePlayer(),
                    ).run(executor::execute)
                }
            }
        }
    }

    private fun createTpaAcceptNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpaccept") {
                runs { ctx ->
                    TpaCommand.TpaAccept(
                        executorPlayer = ctx.requirePlayer(),
                    ).run(executor::execute)
                }
            }
        }
    }

    private fun createTpaDenyNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpadeny") {
                runs { ctx ->
                    TpaCommand.TpaDeny(
                        executorPlayer = ctx.requirePlayer(),
                    ).run(executor::execute)
                }
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
