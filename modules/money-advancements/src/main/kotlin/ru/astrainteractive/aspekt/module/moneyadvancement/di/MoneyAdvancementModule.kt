package ru.astrainteractive.aspekt.module.moneyadvancement.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneyadvancement.event.MoneyAdvancementEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class MoneyAdvancementModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) : Logger by JUtiltLogger("MoneyAdvancementModule") {
    private val moneyAdvancementEvent = MoneyAdvancementEvent(
        configurationProvider = coreModule.configKrate,
        currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
        kyoriComponentSerializerProvider = coreModule.kyoriKrate,
        translationProvider = coreModule.translation
    )

    val lifecycle: Lifecycle by lazy {
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
