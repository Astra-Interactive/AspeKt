@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.tpa.command

import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType
import ru.astrainteractive.astralibs.command.util.argument
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.hints
import ru.astrainteractive.astralibs.command.util.requireArgument
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.server.util.ForgeUtil
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer
import ru.astrainteractive.astralibs.server.util.getOnlinePlayer
import ru.astrainteractive.astralibs.server.util.getOnlinePlayers
import ru.astrainteractive.astralibs.server.util.toPlain

@Suppress("LongMethod")
internal fun RegisterCommandsEvent.tpa(
    tpaCommandExecutor: TpaCommandExecutor
) {
    command("tpa") {
        argument("player", com.mojang.brigadier.arguments.StringArgumentType.string()) {
            hints(ForgeUtil.getOnlinePlayers().map { it.name.toPlain() })

            runs { ctx ->
                val targetPlayerName = ctx.requireArgument("player", StringArgumentType)
                TpaCommand.TpaTo(
                    executorPlayer = ctx.source.player
                        ?.asOnlineMinecraftPlayer()
                        ?: return@runs,
                    targetPlayer = ForgeUtil.getOnlinePlayer(targetPlayerName)
                        ?.asOnlineMinecraftPlayer()
                        ?: return@runs
                ).run(tpaCommandExecutor::execute)
            }
        }
    }.run(dispatcher::register)

    command("tpahere") {
        argument("player", com.mojang.brigadier.arguments.StringArgumentType.string()) {
            runs { ctx ->
                val targetPlayerName = ctx.requireArgument("player", StringArgumentType)
                TpaCommand.TpaHere(
                    executorPlayer = ctx.source.player
                        ?.asOnlineMinecraftPlayer()
                        ?: return@runs,
                    targetPlayer = ForgeUtil.getOnlinePlayer(targetPlayerName)
                        ?.asOnlineMinecraftPlayer()
                        ?: return@runs
                ).run(tpaCommandExecutor::execute)
            }
        }
    }.run(dispatcher::register)

    command("tpacancel") {
        runs { ctx ->
            TpaCommand.TpaCancel(
                executorPlayer = ctx.source.player
                    ?.asOnlineMinecraftPlayer()
                    ?: return@runs,
            ).run(tpaCommandExecutor::execute)
        }
    }.run(dispatcher::register)

    command("tpaccept") {
        runs { ctx ->
            TpaCommand.TpaAccept(
                executorPlayer = ctx.source.player
                    ?.asOnlineMinecraftPlayer()
                    ?: return@runs,
            ).run(tpaCommandExecutor::execute)
        }
    }.run(dispatcher::register)

    command("tpadeny") {
        runs { ctx ->
            TpaCommand.TpaDeny(
                executorPlayer = ctx.source.player
                    ?.asOnlineMinecraftPlayer()
                    ?: return@runs,
            ).run(tpaCommandExecutor::execute)
        }
    }
}
