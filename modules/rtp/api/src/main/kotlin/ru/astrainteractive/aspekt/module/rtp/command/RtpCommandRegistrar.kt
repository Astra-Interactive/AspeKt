package ru.astrainteractive.aspekt.module.rtp.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.api.registrar.CommandRegistrarContext

/**
 * Platform-agnostic RTP command registrar. Builds and registers Brigadier node for:
 * /rtp
 */
class RtpCommandRegistrar(
    private val executor: RtpCommandExecutor,
    private val safeLocationProvider: SafeLocationProvider,
    private val multiplatformCommand: MultiplatformCommand,
    private val registrarContext: CommandRegistrarContext
) {
    private fun createNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command(alias = "rtp") {
                runs { ctx ->
                    val player = ctx.requirePlayer()
                    RtpCommand(
                        player = player,
                        nextTickTime = safeLocationProvider.getNextTickTime()
                    ).run(executor::execute)
                }
            }
        }
    }

    fun register() {
        registrarContext.registerWhenReady(createNode())
    }
}
