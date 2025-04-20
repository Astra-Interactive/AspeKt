package ru.astrainteractive.aspekt.module.rtp.command

import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.RegisterCommandsEvent
import ru.astrainteractive.aspekt.core.forge.command.util.literal
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getAverageTickTime
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.util.tryCast

fun RegisterCommandsEvent.rtp(rtpCommandExecutor: RtpCommandExecutor) {
    literal(
        alias = "rtp",
        execute = { ctx ->
            val player = ctx.source.player?.tryCast<ServerPlayer>() ?: return@literal

            RtpCommand(
                player = OnlineMinecraftPlayer(player.uuid, player.name.toPlain()),
                averageTickTime = ForgeUtil.getAverageTickTime()
            ).run(rtpCommandExecutor::execute)
        }
    ).run(dispatcher::register)
}
