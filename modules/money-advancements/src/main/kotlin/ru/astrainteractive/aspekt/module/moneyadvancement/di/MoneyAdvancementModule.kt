package ru.astrainteractive.aspekt.module.moneyadvancement.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneyadvancement.event.MoneyAdvancementEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

interface MoneyAdvancementModule {
    val lifecycle: Lifecycle

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : MoneyAdvancementModule, Logger by JUtiltLogger("MoneyAdvancementModule") {
        private val moneyAdvancementEvent = MoneyAdvancementEvent(
            configurationProvider = coreModule.pluginConfig,
            currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
            kyoriComponentSerializerProvider = coreModule.kyoriComponentSerializer,
            translationProvider = coreModule.translation
        )

        override val lifecycle: Lifecycle by lazy {
            Lifecycle.Lambda(
                onEnable = {
                    moneyAdvancementEvent.onEnable(bukkitCoreModule.plugin)
                },
                onDisable = {
                    moneyAdvancementEvent.onDisable()
                },
            )
        }
    }
}
