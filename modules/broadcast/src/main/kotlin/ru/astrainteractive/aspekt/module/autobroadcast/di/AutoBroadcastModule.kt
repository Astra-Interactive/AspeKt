package ru.astrainteractive.aspekt.module.autobroadcast.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autobroadcast.job.AutoBroadcastJob
import ru.astrainteractive.aspekt.module.autobroadcast.job.ScheduledJob
import ru.astrainteractive.aspekt.util.Lifecycle
import ru.astrainteractive.klibs.kdi.Factory

interface AutoBroadcastModule {
    val autoBroadcastLifecycleFactory: Factory<Lifecycle>

    class Default(coreModule: CoreModule) : AutoBroadcastModule {
        private val autoBroadcastJob: ScheduledJob by lazy {
            val dependencies = AutoBroadcastDependencies.Default(coreModule)
            AutoBroadcastJob(dependencies)
        }
        override val autoBroadcastLifecycleFactory = Factory {
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
