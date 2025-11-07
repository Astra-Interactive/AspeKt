package ru.astrainteractive.aspekt.module.sethome.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.server.util.asLocatable
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer

/**
 * Forge SetHome related commands registrar. Registers:
 * - /sethome <home_name>
 * - /delhome <home_name>
 * - /home <home_name>
 */
class SetHomeCommandRegistrar(
    private val homeKrateProvider: HomeKrateProvider,
    private val executor: HomeCommandExecutor
) {
    private fun createSetHomeNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("sethome") {
            argument("home_name", StringArgumentType.string()) { homeNameArg ->
                runs { ctx ->
                    val player = ctx.source.player ?: return@runs
                    HomeCommand.SetHome(
                        playerData = player.asOnlineMinecraftPlayer(),
                        playerHome = PlayerHome(
                            location = player.asLocatable().getLocation(),
                            name = ctx.requireArgument(homeNameArg)
                        )
                    ).run(executor::execute)
                }
            }
        }
    }

    private fun createDelHomeNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("delhome") {
            argument("home_name", StringArgumentType.string()) { homeNameArg ->
                hints { ctx ->
                    val player = ctx.source.player ?: return@hints emptyList()
                    homeKrateProvider.get(player.uuid).cachedStateFlow.value.map(PlayerHome::name)
                }
                runs { ctx ->
                    val player = ctx.source.player ?: return@runs
                    HomeCommand.DelHome(
                        playerData = player.asOnlineMinecraftPlayer(),
                        homeName = ctx.requireArgument(homeNameArg)
                    ).run(executor::execute)
                }
            }
        }
    }

    private fun createHomeNode(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("home") {
            argument("home_name", StringArgumentType.string()) { homeNameArg ->
                hints { ctx ->
                    val player = ctx.source.player ?: return@hints emptyList()
                    homeKrateProvider.get(player.uuid).cachedStateFlow.value.map(PlayerHome::name)
                }
                runs { ctx ->
                    val player = ctx.source.player ?: return@runs
                    HomeCommand.TpHome(
                        playerData = player.asOnlineMinecraftPlayer(),
                        homeName = ctx.requireArgument(homeNameArg)
                    ).run(executor::execute)
                }
            }
        }
    }

    fun createNodes() = listOf(
        createSetHomeNode(),
        createDelHomeNode(),
        createHomeNode()
    )
}
