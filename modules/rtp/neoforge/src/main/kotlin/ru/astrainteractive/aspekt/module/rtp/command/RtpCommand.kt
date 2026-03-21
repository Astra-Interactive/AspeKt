package ru.astrainteractive.aspekt.module.rtp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.neoforged.neoforge.event.RegisterCommandsEvent
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.util.NeoForgeUtil
import ru.astrainteractive.astralibs.server.util.getNextTickTime

fun RegisterCommandsEvent.rtp(
    rtpCommandExecutor: RtpCommandExecutor,
    multiplatformCommand: MultiplatformCommand
): LiteralArgumentBuilder<Any> {
    return with(multiplatformCommand) {
        command(alias = "rtp") {
            runs { ctx ->
                val player = ctx.requirePlayer()
                RtpCommand(
                    player = player,
                    nextTickTime = NeoForgeUtil.getNextTickTime()
                ).run(rtpCommandExecutor::execute)
            }
        }
    }
}
