package ru.astrainteractive.aspekt.module.autobroadcast.di

import ru.astrainteractive.aspekt.module.autobroadcast.job.AutoBroadcastJob
import ru.astrainteractive.aspekt.module.autobroadcast.job.ScheduledJob
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.util.Lifecycle

interface AutoBroadcastModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : AutoBroadcastModule {
        private val autoBroadcastJob: ScheduledJob by lazy {
            val dependencies = AutoBroadcastDependencies.Default(coreModule)
            AutoBroadcastJob(dependencies)
        }
        override val lifecycle: Lifecycle = object : Lifecycle {
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
}
