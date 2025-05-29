package ru.astrainteractive.aspekt.module.rtp.command

import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.command
import ru.astrainteractive.aspekt.core.forge.command.util.runs
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.asOnlineMinecraftPlayer
import ru.astrainteractive.aspekt.core.forge.util.getNextTickTime
import ru.astrainteractive.aspekt.util.tryCast

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
