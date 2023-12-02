package ru.astrainteractive.aspekt.event.sort.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.di.CoreModule
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
        coreModule: CoreModule
    ) : SortDependencies {
        override val eventListener: EventListener by Provider {
            coreModule.eventListener.value
        }
        override val plugin: AspeKt by Provider {
            coreModule.plugin.value
        }
        override val sortController: SortController by Single {
            SortController()
        }
    }
}
