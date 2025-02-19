package ru.astrainteractive.aspekt.module.treecapitator.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.treecapitator.event.tc.TCEvent
import ru.astrainteractive.aspekt.module.treecapitator.event.tc.di.TCDependencies
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class TreeCapitatorModule(
    coreModule: CoreModule
) {
    private val deps = TCDependencies.Default(coreModule)
    private val tcEvent = TCEvent(deps)

    val lifecycle = Lifecycle.Lambda(
        onEnable = {
            tcEvent.onEnable(coreModule.plugin)
        },
        onDisable = {
            tcEvent.onDisable()
        }
    )
}