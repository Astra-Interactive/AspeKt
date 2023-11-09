package ru.astrainteractive.aspekt.event.sort.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.sort.SortController
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue

interface SortDependencies {
    val eventListener: EventListener
    val plugin: AspeKt
    val sortController: SortController

    class Default(
        rootModule: RootModule
    ) : SortDependencies {
        override val eventListener: EventListener by Provider {
            rootModule.eventListener.value
        }
        override val plugin: AspeKt by Provider {
            rootModule.plugin.value
        }
        override val sortController: SortController by Single {
            SortController()
        }
    }
}