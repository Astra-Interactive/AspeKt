package ru.astrainteractive.aspekt.module.tpa.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.bridge.PlatformServer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

internal class TpaCommandRegistrar(
    private val executor: TpaCommandExecutor,
    private val platformServer: PlatformServer,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private fun createTpaNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("tpa") {
                argument(
                    "player",
                    StringArgumentType.string()
                ) { playerArg ->
                    hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                    runs(commandExceptionHandler::onException) { ctx ->
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
                argument(
                    "player",
                    StringArgumentType.string()
                ) { playerArg ->
                    hints { platformServer.getOnlinePlayers().map(OnlineKPlayer::name) }
                    runs(commandExceptionHandler::onException) { ctx ->
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
                runs(commandExceptionHandler::onException) { ctx ->
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
                runs(commandExceptionHandler::onException) { ctx ->
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
                runs(commandExceptionHandler::onException) { ctx ->
                    TpaCommand.TpaDeny(
                        executorPlayer = ctx.requirePlayer()
                    ).run(executor::execute)
                }
            }
        }
    }

    fun createNodes(): List<LiteralArgumentBuilder<Any>> {
        return listOf(
            createTpaNode(),
            createTpaHereNode(),
            createTpaCancelNode(),
            createTpaAcceptNode(),
            createTpaDenyNode()
        )
    }
}
