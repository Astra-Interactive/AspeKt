package ru.astrainteractive.aspekt.command.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import ru.astrainteractive.aspekt.command.reload.ReloadLiteralArgumentBuilder
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.astralibs.command.api.registrar.registerWhenReady
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class CommandsModule(
    private val coreModule: CoreModule,
    lifecyclePlugin: Lifecycle
) {
    private val moduleUnconfinedScope = CoroutineScope(coreModule.unconfinedScope.coroutineContext + SupervisorJob())

    private val nodes = listOf(
        ReloadLiteralArgumentBuilder(
            translationKrate = coreModule.translationKrate,
            kyoriKrate = coreModule.kyoriKrate,
            lifecyclePlugin = lifecyclePlugin,
            multiplatformCommand = coreModule.multiplatformCommand
        ).create(),
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            coreModule.commandRegistrarContext.registerWhenReady(nodes, moduleUnconfinedScope)
        },
        onDisable = {
            moduleUnconfinedScope.cancel()
        }
    )
}
