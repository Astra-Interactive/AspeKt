package ru.astrainteractive.aspekt.module.sethome.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.util.tryCast

/**
 * Forge SetHome related commands registrar. Registers:
 * - /sethome <home_name>
 * - /delhome <home_name>
 * - /home <home_name>
 */
class SetHomeLiteralArgumentBuilder(
    private val homeKrateProvider: HomeKrateProvider,
    private val executor: HomeCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand
) {
    private fun createSetHomeNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("sethome") {
                argument("home_name", StringArgumentType.string()) { homeNameArg ->
                    runs { ctx ->
                        val player = ctx.requirePlayer()
                        HomeCommand.SetHome(
                            playerData = player,
                            playerHome = PlayerHome(
                                location = player.getLocation(),
                                name = ctx.requireArgument(homeNameArg)
                            )
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    private fun createDelHomeNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("delhome") {
                argument("home_name", StringArgumentType.string()) { homeNameArg ->
                    hints { ctx ->

                        val player = ctx.getSender().tryCast<OnlineKPlayer>() ?: return@hints emptyList()
                        homeKrateProvider.get(player.uuid).cachedStateFlow.value.map(PlayerHome::name)
                    }
                    runs { ctx ->
                        val player = ctx.requirePlayer()
                        HomeCommand.DelHome(
                            playerData = player,
                            homeName = ctx.requireArgument(homeNameArg)
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    private fun createHomeNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command("home") {
                argument("home_name", StringArgumentType.string()) { homeNameArg ->
                    hints { ctx ->
                        val player = ctx.getSender().tryCast<OnlineKPlayer>() ?: return@hints emptyList()
                        homeKrateProvider.get(player.uuid).cachedStateFlow.value.map(PlayerHome::name)
                    }
                    runs { ctx ->
                        val player = ctx.requirePlayer()
                        HomeCommand.TpHome(
                            playerData = player,
                            homeName = ctx.requireArgument(homeNameArg)
                        ).run(executor::execute)
                    }
                }
            }
        }
    }

    fun create() = listOf(
        createSetHomeNode(),
        createDelHomeNode(),
        createHomeNode()
    )
}
