package ru.astrainteractive.aspekt.module.antiswear.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.antiswear.event.AntiSwearEventListener
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface AntiSwearModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : AntiSwearModule {
        private val antiSwearEventListener = AntiSwearEventListener()
        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                antiSwearEventListener.onEnable(plugin = coreModule.plugin.value)
            },
            onDisable = {
                antiSwearEventListener.onDisable()
            }
        )
    }
}
