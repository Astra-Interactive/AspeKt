package ru.astrainteractive.aspekt.module.newbee.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.newbee.event.NewBeeEventListener
import ru.astrainteractive.aspekt.module.newbee.event.di.EventDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface NewBeeModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule
    ) : NewBeeModule {
        private val dependencies: EventDependencies by lazy {
            EventDependencies.Default(
                translation = coreModule.translation.value,
                kyoriComponentSerializer = coreModule.kyoriComponentSerializer.value,
                scope = coreModule.scope.value,
                dispatcher = coreModule.dispatchers.value
            )
        }

        private val newBeeEventListener: NewBeeEventListener by lazy {
            NewBeeEventListener(dependencies)
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                newBeeEventListener.onEnable(coreModule.plugin.value)
            },
            onDisable = {
                newBeeEventListener.onDisable()
            }
        )
    }
}
