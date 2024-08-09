package ru.astrainteractive.aspekt.module.moneyadvancement.di

import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneyadvancement.event.MoneyAdvancementEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle

interface MoneyAdvancementModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule
    ) : MoneyAdvancementModule {
        private val moneyAdvancementEvent = MoneyAdvancementEvent(
            configurationProvider = { coreModule.pluginConfig.value },
            economyProvider = { coreModule.economyProvider.value },
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
                }
            )
        }
    }
}
