package ru.astrainteractive.aspekt.module.rtp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.server.util.NeoForgeUtil
import ru.astrainteractive.astralibs.server.util.getNextTickTime

/**
 * Forge RTP command registrar. Builds and registers Brigadier node for:
 * /rtp
 */
class RtpLiteralArgumentBuilder(
    private val executor: RtpCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand
) {
    fun create(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command(alias = "rtp") {
                runs { ctx ->
                    val player = ctx.requirePlayer()
                    RtpCommand(
                        player = player,
                        nextTickTime = NeoForgeUtil.getNextTickTime()
                    ).run(executor::execute)
                }
            }
        }
    }
}
