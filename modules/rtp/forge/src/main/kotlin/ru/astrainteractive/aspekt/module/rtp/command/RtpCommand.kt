package ru.astrainteractive.aspekt.module.rtp.command

import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.runs
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getNextTickTime
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.util.tryCast

fun RegisterCommandsEvent.rtp(rtpCommandExecutor: RtpCommandExecutor) {
    command(alias = "rtp") {
        runs { ctx ->
            val player = ctx.source.player?.tryCast<ServerPlayer>() ?: return@runs
            RtpCommand(
                player = OnlineMinecraftPlayer(player.uuid, player.name.toPlain()),
                nextTickTime = ForgeUtil.getNextTickTime()
            ).run(rtpCommandExecutor::execute)
        }
    }.run(dispatcher::register)
}
