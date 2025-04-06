package ru.astrainteractive.aspekt.module.newbee.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.newbee.event.NewBeeEventListener
import ru.astrainteractive.aspekt.module.newbee.event.di.EventDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface NewBeeModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : NewBeeModule {
        private val newBeeEventListener = NewBeeEventListener(
            dependencies = EventDependencies.Default(
                translationKrate = coreModule.translation,
                kyoriComponentSerializerKrate = coreModule.kyoriComponentSerializer,
                scope = coreModule.scope,
                dispatcher = coreModule.dispatchers
            )
        )

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                newBeeEventListener.onEnable(bukkitCoreModule.plugin)
            },
            onDisable = {
                newBeeEventListener.onDisable()
            }
        )
    }
}
