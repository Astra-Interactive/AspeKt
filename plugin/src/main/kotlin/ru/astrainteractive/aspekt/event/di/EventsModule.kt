package ru.astrainteractive.aspekt.event.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.event.sit.di.SitModule
import ru.astrainteractive.aspekt.event.sort.SortEvent
import ru.astrainteractive.aspekt.event.sort.di.SortDependencies
import ru.astrainteractive.aspekt.event.tc.TCEvent
import ru.astrainteractive.aspekt.event.tc.di.TCDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface EventsModule {
    val lifecycle: Lifecycle
    val sitModule: SitModule

    class Default(coreModule: CoreModule) : EventsModule {

        private val tcEvent: TCEvent by lazy {
            val tcDependencies: TCDependencies = TCDependencies.Default(coreModule)
            TCEvent(tcDependencies)
        }

        private val sortEvent: SortEvent by lazy {
            val sortDependencies: SortDependencies = SortDependencies.Default(coreModule)
            SortEvent(sortDependencies)
        }

        override val sitModule: SitModule by lazy {
            SitModule.Default(coreModule)
        }

        private val restrictionsEvent: RestrictionsEvent by lazy {
            val restrictionsDependencies: RestrictionsDependencies = RestrictionsDependencies.Default(coreModule)
            RestrictionsEvent(restrictionsDependencies)
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                sitModule.onEnable()
                tcEvent
                sortEvent
                restrictionsEvent
            },
            onReload = {
                sitModule.onReload()
            },
            onDisable = {
                sitModule.onDisable()
            }
        )
    }
}
