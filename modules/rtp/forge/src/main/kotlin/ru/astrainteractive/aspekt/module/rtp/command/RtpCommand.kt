package ru.astrainteractive.aspekt.module.rtp.command

import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.astralibs.command.util.command
import ru.astrainteractive.astralibs.command.util.runs
import ru.astrainteractive.astralibs.server.util.ForgeUtil
import ru.astrainteractive.astralibs.server.util.asOnlineMinecraftPlayer
import ru.astrainteractive.astralibs.server.util.getNextTickTime
import ru.astrainteractive.astralibs.util.tryCast

fun RegisterCommandsEvent.rtp(rtpCommandExecutor: RtpCommandExecutor) {
    command(alias = "rtp") {
        runs { ctx ->
            val player = ctx.source.player?.tryCast<ServerPlayer>() ?: return@runs
            RtpCommand(
                player = player.asOnlineMinecraftPlayer(),
                nextTickTime = ForgeUtil.getNextTickTime()
            ).run(rtpCommandExecutor::execute)
        }
    }.run(dispatcher::register)
}
