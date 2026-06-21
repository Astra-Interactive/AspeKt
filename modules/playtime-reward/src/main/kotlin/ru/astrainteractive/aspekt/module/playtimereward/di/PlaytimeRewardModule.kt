@file:OptIn(ExperimentalTime::class)

package ru.astrainteractive.aspekt.module.playtimereward.di

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.playtimereward.controller.PlaytimeRewardController
import ru.astrainteractive.aspekt.module.playtimereward.event.PlaytimeRewardEventListener
import ru.astrainteractive.aspekt.module.playtimereward.krate.PlaytimeRewardKrate
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeRewardConfiguration
import ru.astrainteractive.aspekt.module.playtimereward.service.PlaytimeServiceExecutor
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.service.TickFlowService
import ru.astrainteractive.klibs.kstorage.api.asStateFlowMutableKrate
import java.io.File
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PlaytimeRewardModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {
    private val configKrate = coreModule.yamlFormat
        .krateOf(
            file = coreModule.dataFolder.resolve("playtime_reward.yml"),
            factory = ::PlaytimeRewardConfiguration
        )
        .asStateFlowMutableKrate()

    private val playtimeRewardController = PlaytimeRewardController(
        playtimeRewardKrateFactory = PlaytimeRewardKrate.Factory(
            tempDataFolder = coreModule.dataFolder
                .resolve(".temp")
                .also(File::mkdirs),
            stringFormat = coreModule.jsonStringFormat,
            clock = Clock.System
        ),
        economyFacade = bukkitCoreModule.currencyEconomyProviderFactory.findDefault(),
        playtimeRewardConfigurationKrate = configKrate,
        pluginTranslationKrate = coreModule.translationKrate,
        kyoriKrate = coreModule.kyoriKrate
    )

    private val eventListener = PlaytimeRewardEventListener(
        controller = playtimeRewardController,
        ioScope = coreModule.ioScope
    )

    private val expireService = TickFlowService(
        coroutineContext = SupervisorJob() + coreModule.dispatchers.IO,
        delay = configKrate.cachedStateFlow.map { rewardConfiguration -> rewardConfiguration.checkInterval },
        executor = PlaytimeServiceExecutor(
            playtimeRewardController = playtimeRewardController,
            platformServer = coreModule.platformServer
        )
    )

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                eventListener.onEnable(bukkitCoreModule.plugin)
                expireService.onCreate()
            },
            onDisable = {
                expireService.onDestroy()
                eventListener.onDisable()
            },
            onReload = {
                configKrate.getValue()
            }
        )
    }
}
