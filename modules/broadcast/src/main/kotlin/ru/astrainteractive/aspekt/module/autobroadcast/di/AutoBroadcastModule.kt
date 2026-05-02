package ru.astrainteractive.aspekt.module.autobroadcast.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autobroadcast.job.AutoBroadcastJob
import ru.astrainteractive.aspekt.module.autobroadcast.model.AnnouncementsConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.api.withDefault

class AutoBroadcastModule(coreModule: CoreModule) {

    private val announcementsConfigKrate = coreModule.yamlFormat
        .krateOf<AnnouncementsConfiguration>(coreModule.dataFolder.resolve("announcements.yml"))
        .withDefault(::AnnouncementsConfiguration)
        .asCachedMutableKrate()

    private val autoBroadcastJob = AutoBroadcastJob(
        announcementsConfigKrate = announcementsConfigKrate,
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
                announcementsConfigKrate.getValue()
                onDisable()
                onEnable()
            }
        )
    }
}
