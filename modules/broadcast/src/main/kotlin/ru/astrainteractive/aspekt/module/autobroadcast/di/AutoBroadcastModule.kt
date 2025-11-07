package ru.astrainteractive.aspekt.module.autobroadcast.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autobroadcast.job.AutoBroadcastJob
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

class AutoBroadcastModule(coreModule: CoreModule) {
    private val autoBroadcastJob = AutoBroadcastJob(
        configKrate = coreModule.configKrate,
        kyoriKrate = coreModule.kyoriKrate,
        ioScope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers
    )
    val lifecycle by lazy {
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
