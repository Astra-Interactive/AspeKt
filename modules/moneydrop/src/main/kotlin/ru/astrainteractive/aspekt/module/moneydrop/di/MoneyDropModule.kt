package ru.astrainteractive.aspekt.module.moneydrop.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneydrop.database.di.MoneyDropDaoModule
import ru.astrainteractive.aspekt.module.moneydrop.event.MoneyDropController
import ru.astrainteractive.aspekt.module.moneydrop.event.MoneyDropEvent
import ru.astrainteractive.aspekt.module.moneydrop.model.MoneyDropConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.api.asCachedMutableKrate
import java.io.File
import kotlin.time.Clock

class MoneyDropModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {

    private val moneyDropConfigKrate = coreModule.yamlFormat
        .krateOf(
            file = resolveConfigurationFile(coreModule.dataFolder),
            factory = ::MoneyDropConfiguration
        )
        .asCachedMutableKrate()

    private val moneyDropDaoModule = MoneyDropDaoModule(
        dataFolder = coreModule.dataFolder,
        ioDispatcher = coreModule.dispatchers.IO,
        ioScope = coreModule.ioScope
    )

    private val moneyDropController = MoneyDropController(
        translationDependency = coreModule.translationKrate,
        dispatchers = coreModule.dispatchers,
        moneyDropKrate = moneyDropConfigKrate,
        repository = moneyDropDaoModule.repository,
        clock = Clock.System
    )

    private val moneyDropEvent: MoneyDropEvent = MoneyDropEvent(
        translationKrate = coreModule.translationKrate,
        moneyDropController = moneyDropController,
        currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
        ioScope = coreModule.ioScope
    )

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                moneyDropDaoModule.lifecycle.onEnable()
                moneyDropEvent.onEnable(bukkitCoreModule.plugin)
            },
            onReload = {
                moneyDropDaoModule.lifecycle.onReload()
                moneyDropConfigKrate.getValue()
            },
            onDisable = {
                moneyDropEvent.onDisable()
                moneyDropDaoModule.lifecycle.onDisable()
            }
        )
    }

    companion object {
        fun resolveConfigurationFile(dataFolder: File): File = dataFolder.resolve("money_drop.yml")
    }
}
