package ru.astrainteractive.aspekt.autobroadcast.di

import kotlinx.coroutines.plus
import ru.astrainteractive.aspekt.autobroadcast.AutoBroadcastJob
import ru.astrainteractive.aspekt.autobroadcast.ScheduledJob
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.util.Lifecycle

interface AutoBroadcastModule : Lifecycle {
    val autoBroadcastJob: ScheduledJob

    class Default(coreModule: CoreModule) : AutoBroadcastModule {
        override val autoBroadcastJob: ScheduledJob by lazy {
            val dependencies = AutoBroadcastDependencies.Default(coreModule)
            AutoBroadcastJob(dependencies)
        }

        override fun onEnable() {
            autoBroadcastJob.onEnable()
        }

        override fun onDisable() {
            autoBroadcastJob.onDisable()
        }

        override fun onReload() {
            onDisable()
            onEnable()
        }
    }
}
