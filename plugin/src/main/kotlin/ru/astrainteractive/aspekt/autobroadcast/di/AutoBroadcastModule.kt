package ru.astrainteractive.aspekt.autobroadcast.di

import ru.astrainteractive.aspekt.autobroadcast.AutoBroadcastJob
import ru.astrainteractive.aspekt.autobroadcast.ScheduledJob
import ru.astrainteractive.aspekt.di.RootModule

interface AutoBroadcastModule {
    val autoBroadcastJob: ScheduledJob

    class Default(rootModule: RootModule) : AutoBroadcastModule {
        override val autoBroadcastJob: ScheduledJob by lazy {
            val dependencies = AutoBroadcastDependencies.Default(rootModule)
            AutoBroadcastJob(dependencies)
        }
    }
}
