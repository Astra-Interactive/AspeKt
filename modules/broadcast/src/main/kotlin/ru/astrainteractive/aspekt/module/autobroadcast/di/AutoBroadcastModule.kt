package ru.astrainteractive.aspekt.module.autobroadcast.di

import kotlinx.coroutines.flow.map
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.autobroadcast.model.AnnouncementsConfiguration
import ru.astrainteractive.aspekt.module.autobroadcast.service.AutoBroadcastServiceTask
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.IntervalService
import ru.astrainteractive.klibs.kstorage.api.asStateFlowMutableKrate
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File

class AutoBroadcastModule(coreModule: CoreModule) {

    private val announcementsConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = getConfigurationFile(coreModule.dataFolder),
            factory = ::AnnouncementsConfiguration
        )
        .asStateFlowMutableKrate()

    private val autoBroadcastServiceTask = AutoBroadcastServiceTask(
        announcementsConfigKrate = announcementsConfigKrate,
        kyoriKrate = coreModule.kyoriKrate,
        ioScope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers
    )

    private val autoBroadcastService = IntervalService(
        interval = announcementsConfigKrate.cachedStateFlow.map { configuration -> configuration.interval },
        scope = coreModule.ioScope,
        logger = JUtiltLogger("AutoBroadcastService"),
        task = autoBroadcastServiceTask
    )

    val lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                autoBroadcastService.onEnable()
            },
            onDisable = {
                autoBroadcastService.onDisable()
                autoBroadcastServiceTask.hideShownBossBar()
            },
            onReload = {
                announcementsConfigKrate.getValue()
                autoBroadcastService.onDisable()
                autoBroadcastServiceTask.hideShownBossBar()
                autoBroadcastService.onEnable()
            }
        )
    }

    companion object {
        fun getConfigurationFile(dataFolder: File): File = dataFolder.resolve("announcements.yml")
    }
}
