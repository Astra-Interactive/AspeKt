package ru.astrainteractive.aspekt.event.moneydrop.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.moneydrop.MoneyDropController
import ru.astrainteractive.aspekt.event.moneydrop.MoneyDropEvent

interface MoneyDropModule {
    val moneyDropController: MoneyDropController
    val moneyDropEvent: MoneyDropEvent

    class Default(coreModule: CoreModule) : MoneyDropModule {
        override val moneyDropController: MoneyDropController by lazy {
            MoneyDropController(
                pluginConfigurationDependency = coreModule.pluginConfig,
                translationContext = coreModule.translationContext,
                translationDependency = coreModule.translation
            )
        }
        override val moneyDropEvent: MoneyDropEvent by lazy {
            MoneyDropEvent(
                dependencies = MoneyDropDependencies.Default(
                    coreModule = coreModule,
                    moneyDropController = moneyDropController
                )
            )
        }
    }
}
