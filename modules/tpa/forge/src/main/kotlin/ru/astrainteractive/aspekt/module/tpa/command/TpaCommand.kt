@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.tpa.command

import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.stringArgument
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayers
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType

private fun Player.toMinecraftPlayer(): OnlineMinecraftPlayer {
    return OnlineMinecraftPlayer(
        uuid = uuid,
        name = name.toPlain()
    )
}

@Suppress("LongMethod")
internal fun RegisterCommandsEvent.tpa(
    tpaCommandExecutor: TpaCommandExecutor
) {
    literal("tpa") {
        stringArgument(
            alias = "player",
            suggests = { ForgeUtil.getOnlinePlayers().map { it.name.toPlain() } },
            execute = execute@{ ctx ->
                val targetPlayerName = ctx.requireArgument("player", StringArgumentType)
                TpaCommand.TpaTo(
                    executorPlayer = ctx.source.player
                        ?.toMinecraftPlayer()
                        ?: return@execute,
                    targetPlayer = ForgeUtil.getOnlinePlayer(targetPlayerName)
                        ?.toMinecraftPlayer()
                        ?: return@execute
                ).run(tpaCommandExecutor::execute)
            }
        )
    }.run(dispatcher::register)

    literal("tpahere") {
        stringArgument(
            alias = "player",
            suggests = { ForgeUtil.getOnlinePlayers().map { it.name.toPlain() } },
            execute = execute@{ ctx ->
                val targetPlayerName = ctx.requireArgument("player", StringArgumentType)
                TpaCommand.TpaHere(
                    executorPlayer = ctx.source.player
                        ?.toMinecraftPlayer()
                        ?: return@execute,
                    targetPlayer = ForgeUtil.getOnlinePlayer(targetPlayerName)
                        ?.toMinecraftPlayer()
                        ?: return@execute
                ).run(tpaCommandExecutor::execute)
            }
        )
    }.run(dispatcher::register)

    literal(
        "tpacancel",
        execute = execute@{ ctx ->
            TpaCommand.TpaCancel(
                executorPlayer = ctx.source.player
                    ?.toMinecraftPlayer()
                    ?: return@execute,
            ).run(tpaCommandExecutor::execute)
        },
    ).run(dispatcher::register)

    literal(
        "tpaccept",
        execute = execute@{ ctx ->
            TpaCommand.TpaAccept(
                executorPlayer = ctx.source.player
                    ?.toMinecraftPlayer()
                    ?: return@execute,
            ).run(tpaCommandExecutor::execute)
        },
    ).run(dispatcher::register)

    literal(
        "tpadeny",
        execute = execute@{ ctx ->
            TpaCommand.TpaDeny(
                executorPlayer = ctx.source.player
                    ?.toMinecraftPlayer()
                    ?: return@execute,
            ).run(tpaCommandExecutor::execute)
        },
    ).run(dispatcher::register)
}
