package ru.astrainteractive.aspekt.module.newbee.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.newbee.event.NewBeeEventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class NewBeeModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val newBeeEventListener = NewBeeEventListener(
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translationKrate,
        ioScope = coreModule.ioScope,
        dispatcher = coreModule.dispatchers
    )

    val lifecycle: Lifecycle = Lifecycle.Lambda(
        onEnable = {
            newBeeEventListener.onEnable(bukkitCoreModule.plugin)
        },
        onDisable = {
            newBeeEventListener.onDisable()
        }
    )
}
