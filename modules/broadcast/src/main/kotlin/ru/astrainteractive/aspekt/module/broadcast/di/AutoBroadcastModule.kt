package ru.astrainteractive.aspekt.module.broadcast.di

import kotlinx.coroutines.flow.map
import ru.astrainteractive.aspekt.core.di.CoreModule
import ru.astrainteractive.aspekt.core.util.krateOf
import ru.astrainteractive.aspekt.module.broadcast.model.AnnouncementsConfiguration
import ru.astrainteractive.aspekt.module.broadcast.service.AutoBroadcastServiceTask
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.IntervalService
import ru.astrainteractive.klibs.kstorage.api.asStateFlowMutableKrate
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import java.io.File

class AutoBroadcastModule(coreModule: CoreModule) {

    private val announcementsConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = resolveConfigurationFile(coreModule.dataFolder),
            factory = ::AnnouncementsConfiguration
        )
        .asStateFlowMutableKrate()

    private val autoBroadcastServiceTask = AutoBroadcastServiceTask(
        announcementsConfigKrate = announcementsConfigKrate,
        ioScope = coreModule.ioScope,
        dispatchers = coreModule.dispatchers
    )

    @Suppress("TrivialLambdaReference")
    private val autoBroadcastService = IntervalService(
        interval = announcementsConfigKrate.cachedStateFlow.map { configuration -> configuration.interval },
        scope = coreModule.ioScope,
        logger = JUtiltLogger("AutoBroadcastService"),
        task = autoBroadcastServiceTask
    )

    @Suppress("TrivialLambdaReference")
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
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("announcements.yml")
    }
}
