package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.atemframe.AtemFrameLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.maxonline.MaxOnlineLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.reload.ReloadLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.rtp.RtpLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.rtpbypass.RtpBypassLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.tellchat.TellChatLiteralArgumentBuilder
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for the Bukkit instance module.
 */
class CommonCommandsModule(
    private val bukkitCoreModule: BukkitCoreModule,
    coreModule: CoreModule,
) {
    private val nodes = listOf(
        RtpLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate,
            multiplatformCommand = coreModule.multiplatformCommand,
        ).create(),
        ReloadLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate,
            plugin = bukkitCoreModule.plugin,
            multiplatformCommand = coreModule.multiplatformCommand
        ).create(),
        AtemFrameLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand
        ).create(),
        MaxOnlineLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand
        ).create(),
        TellChatLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            kyoriKrate = coreModule.kyoriKrate
        ).create(),
        RtpBypassLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand
        ).create()
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            nodes.onEach(bukkitCoreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
