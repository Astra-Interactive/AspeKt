package ru.astrainteractive.aspekt.module.antiswear.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.command.SwearCommandRegistry
import ru.astrainteractive.aspekt.module.antiswear.command.di.SwearCommandDependencies
import ru.astrainteractive.aspekt.module.antiswear.data.SwearRepositoryImpl
import ru.astrainteractive.aspekt.module.antiswear.event.AntiSwearEventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface AntiSwearModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : AntiSwearModule {
        private val swearRepository = SwearRepositoryImpl(
            dispatchers = coreModule.dispatchers,
            tempFileStringFormat = coreModule.jsonStringFormat
        )
        private val antiSwearEventListener = AntiSwearEventListener(
            swearRepository = swearRepository,
            scope = coreModule.scope
        )
        private val swearCommandRegistry = SwearCommandRegistry(
            dependencies = SwearCommandDependencies.Default(
                coreModule = coreModule,
                swearRepository = swearRepository
            )
        )
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                antiSwearEventListener.onEnable(plugin = coreModule.plugin)
                swearCommandRegistry.register()
            },
            onDisable = {
                antiSwearEventListener.onDisable()
                swearRepository.clear()
            }
        )
    }
}
