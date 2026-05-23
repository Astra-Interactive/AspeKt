package ru.astrainteractive.aspekt.module.tpa.command.tpa

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommand
import ru.astrainteractive.aspekt.module.tpa.command.TpaCommandExecutor
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

/**
 * Platform-agnostic TPA command registrar. Registers:
 * - /tpa <player>
 * - /tpahere <player>
 * - /tpacancel
 * - /tpaccept
 * - /tpadeny
 */
class TpaCommandRegistrar(
    private val executor: TpaCommandExecutor,
    private val platformServer: PlatformServer,
    private val multiplatformCommand: MultiplatformCommand,
    private val registrarContext: CommandRegistrarContext
) {
    private fun createTpaNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpa") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                    runs { ctx ->
                        val targetPlayerName = ctx.requireArgument(playerArg)
                        TpaCommand.TpaTo(
                            executorPlayer = ctx.requirePlayer(),
                            targetPlayer = platformServer.findOnlinePlayer(targetPlayerName)
                                ?: return@runs
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    private fun createTpaHereNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpahere") {
                argument("player", StringArgumentType.string()) { playerArg ->
                    hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                    runs { ctx ->
                        val targetPlayerName = ctx.requireArgument(playerArg)
                        TpaCommand.TpaHere(
                            executorPlayer = ctx.requirePlayer(),
                            targetPlayer = platformServer.findOnlinePlayer(targetPlayerName)
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
                        executorPlayer = ctx.requirePlayer()
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
                        executorPlayer = ctx.requirePlayer()
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
                        executorPlayer = ctx.requirePlayer()
                    ).run(executor::execute)
                }
            }
        }
    }

    fun register() {
        listOf(
            createTpaNode(),
            createTpaHereNode(),
            createTpaCancelNode(),
            createTpaAcceptNode(),
            createTpaDenyNode()
        ).forEach(registrarContext::registerWhenReady)
    }
}
