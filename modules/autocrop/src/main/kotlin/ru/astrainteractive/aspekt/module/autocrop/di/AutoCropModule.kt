package ru.astrainteractive.aspekt.module.autocrop.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autocrop.AutoCropEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface AutoCropModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : AutoCropModule {
        private val autoCropEvent: AutoCropEvent by lazy {
            val dependencies: AutoCropDependencies = AutoCropDependencies.Default(coreModule)
            AutoCropEvent(dependencies)
        }

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    autoCropEvent.onEnable(coreModule.plugin)
                },
                onDisable = {
                    autoCropEvent.onDisable()
                }
            )
        }
    }
}
