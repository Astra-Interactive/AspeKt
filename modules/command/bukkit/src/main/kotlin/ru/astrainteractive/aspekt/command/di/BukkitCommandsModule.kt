package ru.astrainteractive.aspekt.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.command.atemframe.AtemFrameLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.maxonline.MaxOnlineLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.rtp.RtpLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.rtpbypass.RtpBypassLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.tellchat.TellChatLiteralArgumentBuilder
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

/**
 * Aggregates and registers Brigadier command nodes for the Bukkit instance module.
 */
class BukkitCommandsModule(
    private val bukkitCoreModule: BukkitCoreModule,
    coreModule: CoreModule,
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = listOf(
        RtpLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create(),
        AtemFrameLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create(),
        MaxOnlineLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create(),
        TellChatLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            kyoriKrate = coreModule.kyoriKrate,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create(),
        RtpBypassLiteralArgumentBuilder(
            multiplatformCommand = coreModule.multiplatformCommand,
            commandExceptionHandler = coreModule.commandExceptionHandler
        ).create()
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
