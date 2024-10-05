package ru.astrainteractive.aspekt.module.moneydrop.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropController
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropEvent
import ru.astrainteractive.aspekt.module.moneydrop.database.di.MoneyDropDaoModule
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface MoneyDropModule {
    val lifecycle: Lifecycle

    class Default(coreModule: CoreModule) : MoneyDropModule {
        private val moneyDropDaoModule by lazy {
            MoneyDropDaoModule.Default(
                dataFolder = coreModule.plugin.dataFolder,
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
        private val moneyDropEvent: MoneyDropEvent by lazy {
            MoneyDropEvent(
                dependencies = MoneyDropDependencies.Default(
                    coreModule = coreModule,
                    moneyDropController = moneyDropController,
                )
            )
        }
        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    moneyDropEvent
                }
            )
        }
    }
}
