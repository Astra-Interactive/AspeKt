package ru.astrainteractive.aspekt.module.moneyadvancement.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneyadvancement.event.MoneyAdvancementEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kdi.Reloadable

interface MoneyAdvancementModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule
    ) : MoneyAdvancementModule, Logger by JUtiltLogger("MoneyAdvancementModule") {
        private val economyProvider = Reloadable {
            coreModule.pluginConfig.value.advancementMoney.currencyName
                ?.let(coreModule::findEconomyProviderByCurrency)
                ?: coreModule.defaultEconomyProvider.value
        }

        private val moneyAdvancementEvent = MoneyAdvancementEvent(
            configurationProvider = { coreModule.pluginConfig.value },
            economyProvider = { economyProvider.value },
            kyoriComponentSerializerProvider = { coreModule.kyoriComponentSerializer.value },
            translationProvider = { coreModule.translation.value }
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    moneyAdvancementEvent.onEnable(coreModule.plugin.value)
                },
                onDisable = {
                    moneyAdvancementEvent.onDisable()
                },
                onReload = {
                    economyProvider.reload()
                }
            )
        }
    }
}
