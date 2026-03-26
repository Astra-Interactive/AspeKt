package ru.astrainteractive.aspekt.module.autobroadcast.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autobroadcast.job.AutoBroadcastJob
import ru.astrainteractive.aspekt.module.autobroadcast.model.AnnouncementsConfiguration
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.util.withDefault

class AutoBroadcastModule(coreModule: CoreModule) {

    private val announcementsKrate = coreModule.yamlFormat
        .krateOf<AnnouncementsConfiguration>(coreModule.dataFolder.resolve("announcements.yml"))
        .withDefault(::AnnouncementsConfiguration)
        .asCachedMutableKrate()

    private val autoBroadcastJob = AutoBroadcastJob(
        configKrate = announcementsKrate,
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
