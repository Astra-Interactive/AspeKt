package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.command.reload.ReloadLiteralArgumentBuilder
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class CommandsModule(
    coreModule: CoreModule,
    lifecyclePlugin: Lifecycle
) {
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
            nodes.onEach(coreModule.commandRegistrarContext::registerWhenReady)
        }
    )
}
