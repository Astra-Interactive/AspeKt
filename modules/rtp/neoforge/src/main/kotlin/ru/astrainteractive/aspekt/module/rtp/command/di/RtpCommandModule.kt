package ru.astrainteractive.aspekt.module.rtp.command.di

import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.RtpLiteralArgumentBuilder
import ru.astrainteractive.astralibs.command.api.brigadier.command.MultiplatformCommand
import ru.astrainteractive.astralibs.command.registrar.NeoForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for RTP (Forge) module.
 */
class RtpCommandModule(
    private val commandRegistrarContext: NeoForgeCommandRegistrarContext,
    private val executor: RtpCommandExecutor,
    private val multiplatformCommand: MultiplatformCommand
) {
    private val nodes = buildList {
        RtpLiteralArgumentBuilder(
            executor = executor,
            multiplatformCommand = multiplatformCommand
        ).create().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            nodes.onEach(commandRegistrarContext::registerWhenReady)
        }
    )
}
