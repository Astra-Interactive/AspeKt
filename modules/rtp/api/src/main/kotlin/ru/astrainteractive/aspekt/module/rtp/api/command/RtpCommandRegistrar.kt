@file:Suppress("FieldDependencyConstruction")

package ru.astrainteractive.aspekt.module.rtp.api.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ru.astrainteractive.aspekt.core.command.api.CommandExceptionHandler
import ru.astrainteractive.aspekt.module.rtp.api.api.SafeLocationProvider
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand

internal class RtpCommandRegistrar(
    private val executor: RtpCommandExecutor,
    private val safeLocationProvider: SafeLocationProvider,
    private val multiplatformCommand: MultiplatformCommand,
    private val commandExceptionHandler: CommandExceptionHandler
) {
    private fun createNode(): LiteralArgumentBuilder<Any> {
        return with(multiplatformCommand) {
            command(alias = "rtp") {
                runs(commandExceptionHandler::onException) { ctx ->
                    val player = ctx.requirePlayer()
                    RtpCommand(
                        player = player,
                        ticksPerSecond = safeLocationProvider.resolveNextTickTime()
                    ).run(executor::execute)
                }
            }
        }
    }

    fun createNodes(): List<LiteralArgumentBuilder<Any>> {
        return listOf(createNode())
    }
}
