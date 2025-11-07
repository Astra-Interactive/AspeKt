package ru.astrainteractive.aspekt.module.rtp.command.di

import kotlinx.coroutines.flow.onEach
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandExecutor
import ru.astrainteractive.aspekt.module.rtp.command.RtpCommandRegistrar
import ru.astrainteractive.astralibs.command.registrar.ForgeCommandRegistrarContext
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for RTP (Forge) module.
 */
class RtpCommandModule(
    private val commandRegistrarContext: ForgeCommandRegistrarContext,
    private val executor: RtpCommandExecutor
) {
    private val nodes = buildList {
        RtpCommandRegistrar(executor).createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onDisable = {
            nodes.onEach(commandRegistrarContext::registerWhenReady)
        }
    )
}
