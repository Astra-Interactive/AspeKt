package ru.astrainteractive.aspekt.event.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.inventorysort.event.sort.SortEvent
import ru.astrainteractive.aspekt.inventorysort.event.sort.di.SortDependencies
import ru.astrainteractive.aspekt.module.treecapitator.event.tc.TCEvent
import ru.astrainteractive.aspekt.module.treecapitator.event.tc.di.TCDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface EventsModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : EventsModule {

        private val tcEvent: TCEvent by lazy {
            val tcDependencies: TCDependencies = TCDependencies.Default(coreModule)
            TCEvent(tcDependencies)
        }

        private val sortEvent: SortEvent by lazy {
            val sortDependencies: SortDependencies = SortDependencies.Default(coreModule)
            SortEvent(sortDependencies)
        }

        private val restrictionsEvent: RestrictionsEvent by lazy {
            val restrictionsDependencies: RestrictionsDependencies = RestrictionsDependencies.Default(coreModule)
            RestrictionsEvent(restrictionsDependencies)
        }

        override val lifecycle: Lifecycle = Lifecycle.Lambda(
            onEnable = {
                tcEvent.onEnable(coreModule.plugin)
                sortEvent.onEnable(coreModule.plugin)
                restrictionsEvent.onEnable(coreModule.plugin)
            },
            onReload = {
            },
            onDisable = {
                tcEvent.onDisable()
                sortEvent.onDisable()
                restrictionsEvent.onDisable()
            }
        )
    }
}
