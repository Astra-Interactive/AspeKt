package ru.astrainteractive.aspekt.module.autobroadcast.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.job.ScheduledJob
import ru.astrainteractive.aspekt.module.autobroadcast.job.AutoBroadcastJob
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface AutoBroadcastModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : AutoBroadcastModule {
        private val autoBroadcastJob: ScheduledJob by lazy {
            val dependencies = AutoBroadcastDependencies.Default(coreModule)
            AutoBroadcastJob(dependencies)
        }
        override val lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    autoBroadcastJob.onEnable()
                },
                onDisable = {
                    autoBroadcastJob.onDisable()
                },
                onReload = {
                    onDisable()
                    onEnable()
                }
            )
        }
    }
}
