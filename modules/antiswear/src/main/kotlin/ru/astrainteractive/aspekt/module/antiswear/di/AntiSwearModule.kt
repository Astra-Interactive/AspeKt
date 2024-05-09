package ru.astrainteractive.aspekt.module.antiswear.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.SwearCommandRegistry
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepositoryImpl
import ru.astrainteractive.aspekt.module.antiswear.event.AntiSwearEventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface AntiSwearModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : AntiSwearModule {
        private val swearRepository = SwearRepositoryImpl(
            dispatchers = coreModule.dispatchers.value,
            tempFileStringFormat = coreModule.tempFileStringFormat
        )
        private val antiSwearEventListener = AntiSwearEventListener(
            swearRepository = swearRepository,
            scope = coreModule.scope.value
        )
        private val swearCommandRegistry = SwearCommandRegistry(
            plugin = coreModule.plugin.value,
            translation = coreModule.translation.value,
            kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value,
            scope = coreModule.scope.value,
            swearRepository = swearRepository
        )
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                antiSwearEventListener.onEnable(plugin = coreModule.plugin.value)
                swearCommandRegistry.register()
            },
            onDisable = {
                antiSwearEventListener.onDisable()
            }
        )
    }
}
