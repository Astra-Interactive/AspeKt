package ru.astrainteractive.aspekt.command.bukkit.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.command.bukkit.atemframe.command.AtemFrameLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.bukkit.maxonline.command.MaxOnlineLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.bukkit.rtp.command.RtpLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.bukkit.rtpbypass.command.RtpBypassLiteralArgumentBuilder
import ru.astrainteractive.aspekt.command.bukkit.tellchat.command.TellChatLiteralArgumentBuilder
import ru.astrainteractive.aspekt.core.di.BukkitCoreModule
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class BukkitCommandsModule(
    bukkitCoreModule: BukkitCoreModule,
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

    @Suppress("TrivialLambdaReference")
    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            bukkitCoreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
