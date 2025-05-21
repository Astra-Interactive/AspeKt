@file:Suppress("MatchingDeclarationName")

package ru.astrainteractive.aspekt.module.tpa.command

import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.argument
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.hints
import ru.astrainteractive.aspekt.core.forge.command.util.requireArgument
import ru.astrainteractive.aspekt.core.forge.command.util.runs
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.asOnlineMinecraftPlayer
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayers
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.astralibs.command.api.argumenttype.StringArgumentType

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
