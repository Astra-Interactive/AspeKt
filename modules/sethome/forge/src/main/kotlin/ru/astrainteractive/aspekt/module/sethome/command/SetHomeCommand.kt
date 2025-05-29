@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.sethome.command

import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.command.util.stringArgument
import ru.astrainteractive.astralibs.server.util.asLocatable
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer

@Suppress("LongMethod")
internal fun RegisterCommandsEvent.homes(
    homeKrateProvider: HomeKrateProvider,
    homeCommandExecutor: HomeCommandExecutor
) {
    command("sethome") {
        stringArgument("home_name") {
            runs { ctx ->
                val player = ctx.source.player ?: return@runs
                HomeCommand.SetHome(
                    playerData = player.asOnlineMinecraftPlayer(),
                    playerHome = PlayerHome(
                        location = player.asLocatable().getLocation(),
                        name = ctx.requireArgument("home_name", StringArgumentType)
                    ),
                ).run(homeCommandExecutor::execute)
            }
        }
    }.run(dispatcher::register)

    command("delhome") {
        stringArgument("home_name") {
            hints { ctx ->
                val player = ctx.source.player ?: return@hints emptyList()
                homeKrateProvider.get(player.uuid).cachedStateFlow.value.map(PlayerHome::name)
            }
            runs { ctx ->
                val player = ctx.source.player ?: return@runs
                HomeCommand.DelHome(
                    playerData = player.asOnlineMinecraftPlayer(),
                    homeName = ctx.requireArgument("home_name", StringArgumentType)
                ).run(homeCommandExecutor::execute)
            }
        }
    }.run(dispatcher::register)

    command("home") {
        stringArgument("home_name") {
            hints { ctx ->
                val player = ctx.source.player ?: return@hints emptyList()
                homeKrateProvider.get(player.uuid).cachedStateFlow.value.map(PlayerHome::name)
            }
            runs { ctx ->
                val player = ctx.source.player ?: return@runs
                HomeCommand.TpHome(
                    playerData = player.asOnlineMinecraftPlayer(),
                    homeName = ctx.requireArgument("home_name", StringArgumentType)
                ).run(homeCommandExecutor::execute)
            }
        }
    }.run(dispatcher::register)
}
