package ru.astrainteractive.aspekt.module.restrictions.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.restrictions.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.module.restrictions.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class RestrictionModule(coreModule: CoreModule) {
    private val restrictionsEvent: RestrictionsEvent by lazy {
        val restrictionsDependencies: RestrictionsDependencies = RestrictionsDependencies.Default(coreModule)
        RestrictionsEvent(restrictionsDependencies)
    }
    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            restrictionsEvent.onEnable(coreModule.plugin)
        },
        onDisable = {
            restrictionsEvent.onDisable()
        }
    )
}