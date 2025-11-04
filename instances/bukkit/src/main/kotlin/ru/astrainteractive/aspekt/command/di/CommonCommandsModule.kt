package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.atemframe.AtemFrameCommandRegistrar
import ru.astrainteractive.aspekt.command.maxonline.MaxOnlineCommandRegistrar
import ru.astrainteractive.aspekt.command.reload.ReloadCommandRegistrar
import ru.astrainteractive.aspekt.command.rtp.RtpCommandRegistrar
import ru.astrainteractive.aspekt.command.rtpbypass.RtpBypassedCommandRegistrar
import ru.astrainteractive.aspekt.command.tellchat.TellChatCommandRegistrar
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for the Bukkit instance module.
 */
class CommonCommandsModule(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule
) {
    private val nodes = buildList {
        RtpCommandRegistrar(
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
        ).createNode().run(::add)
        ReloadCommandRegistrar(
            translationKrate = coreModule.translation,
            kyoriKrate = coreModule.kyoriKrate,
            plugin = bukkitCoreModule.plugin
        ).createNode().run(::add)
        AtemFrameCommandRegistrar()
            .createNode().run(::add)
        MaxOnlineCommandRegistrar()
            .createNode().run(::add)
        TellChatCommandRegistrar()
            .createNode().run(::add)
        RtpBypassedCommandRegistrar()
            .createNode().run(::add)
    }

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
