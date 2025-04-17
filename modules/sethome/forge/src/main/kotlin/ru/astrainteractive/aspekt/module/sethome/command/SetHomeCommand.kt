@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.sethome.command

import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.model.getLocation
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.aspekt.module.sethome.util.toHomeLocation
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType

@Suppress("LongMethod")
internal fun RegisterCommandsEvent.homes(
    homeKrateProvider: HomeKrateProvider,
    homeCommandExecutor: HomeCommandExecutor
) {
    literal("sethome") {
        stringArgument(
            alias = "home_name",
            execute = execute@{ ctx ->
                val player = ctx.source.player ?: return@execute
                HomeCommand.SetHome(
                    playerData = OnlineMinecraftPlayer(
                        uuid = player.uuid,
                        name = player.name.toPlain()
                    ),
                    playerHome = PlayerHome(
                        location = player.getLocation().toHomeLocation(),
                        name = ctx.requireArgument("home_name", StringArgumentType)
                    ),
                ).run(homeCommandExecutor::execute)
            }
        )
    }.run(dispatcher::register)

    literal("delhome") {
        stringArgument(
            alias = "home_name",
            suggests = suggests@{ ctx ->
                val player = ctx.source.player ?: return@suggests emptyList()
                homeKrateProvider.get(player.uuid).cachedValue.map(PlayerHome::name)
            },
            execute = execute@{ ctx ->
                val player = ctx.source.player ?: return@execute
                HomeCommand.DelHome(
                    playerData = OnlineMinecraftPlayer(
                        uuid = player.uuid,
                        name = player.name.toPlain()
                    ),
                    homeName = ctx.requireArgument("home_name", StringArgumentType)
                ).run(homeCommandExecutor::execute)
            }
        )
    }.run(dispatcher::register)

    literal("home") {
        stringArgument(
            alias = "home_name",
            suggests = suggests@{ ctx ->
                val player = ctx.source.player ?: return@suggests emptyList()
                homeKrateProvider.get(player.uuid).cachedValue.map(PlayerHome::name)
            },
            execute = execute@{ ctx ->
                val player = ctx.source.player ?: return@execute
                HomeCommand.TpHome(
                    playerData = OnlineMinecraftPlayer(
                        uuid = player.uuid,
                        name = player.name.toPlain()
                    ),
                    homeName = ctx.requireArgument("home_name", StringArgumentType)
                ).run(homeCommandExecutor::execute)
            }
        )
    }.run(dispatcher::register)
}
