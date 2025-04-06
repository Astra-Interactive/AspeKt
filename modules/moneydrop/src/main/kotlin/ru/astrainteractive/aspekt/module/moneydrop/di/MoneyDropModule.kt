package ru.astrainteractive.aspekt.module.moneydrop.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropController
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropEvent
import ru.astrainteractive.aspekt.module.moneydrop.database.di.MoneyDropDaoModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface MoneyDropModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : MoneyDropModule {
        private val moneyDropDaoModule by lazy {
            MoneyDropDaoModule.Default(
                dataFolder = coreModule.dataFolder,
                ioDispatcher = coreModule.dispatchers.IO,
                coroutineScope = coreModule.scope
            )
        }

        private val moneyDropController: MoneyDropController by lazy {
            MoneyDropController(
                pluginConfigurationDependency = coreModule.pluginConfig,
                kyoriComponentSerializerDependency = coreModule.kyoriComponentSerializer,
                translationDependency = coreModule.translation,
                dispatchers = coreModule.dispatchers,
                dao = moneyDropDaoModule.dao
            )
        }

        private val moneyDropEvent: MoneyDropEvent = MoneyDropEvent(
            dependencies = MoneyDropDependencies.Default(
                coreModule = coreModule,
                moneyDropController = moneyDropController,
                bukkitCoreModule = bukkitCoreModule
            )
        )

        override val lifecycle: Lifecycle by lazy {
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
}
