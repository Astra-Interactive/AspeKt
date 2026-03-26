package ru.astrainteractive.aspekt.module.moneydrop.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropController
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropEvent
import ru.astrainteractive.aspekt.module.moneydrop.database.di.MoneyDropDaoModule
import ru.astrainteractive.aspekt.module.moneydrop.model.MoneyDropConfiguration
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.util.withDefault

class MoneyDropModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) {

    private val moneyDropKrate = coreModule.yamlFormat
        .krateOf<MoneyDropConfiguration>(coreModule.dataFolder.resolve("money_drop.yml"))
        .withDefault(::MoneyDropConfiguration)
        .asCachedMutableKrate()

    private val moneyDropDaoModule = MoneyDropDaoModule(
        dataFolder = coreModule.dataFolder,
        ioDispatcher = coreModule.dispatchers.IO,
        coroutineScope = coreModule.ioScope
    )

    private val moneyDropController = MoneyDropController(
        kyoriComponentSerializerDependency = coreModule.kyoriKrate,
        translationDependency = coreModule.translation,
        dispatchers = coreModule.dispatchers,
        moneyDropKrate = moneyDropKrate,
        dao = moneyDropDaoModule.dao
    )

    private val moneyDropEvent: MoneyDropEvent = MoneyDropEvent(
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translation,
        moneyDropController = moneyDropController,
        currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
        ioScope = coreModule.ioScope
    )

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                moneyDropEvent.onEnable(bukkitCoreModule.plugin)
                moneyDropDaoModule.lifecycle.onEnable()
            },
            onDisable = {
                moneyDropDaoModule.lifecycle.onDisable()
                moneyDropEvent.onDisable()
            },
            onReload = {
                moneyDropDaoModule.lifecycle.onReload()
            }
        )
    }
}
