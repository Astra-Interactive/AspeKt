package ru.astrainteractive.aspekt.module.moneyadvancement.di

import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneyadvancement.event.MoneyAdvancementEvent
import ru.astrainteractive.aspekt.module.moneyadvancement.model.MoneyAdvancementsConfiguration
import ru.astrainteractive.aspekt.util.krateOf
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.klibs.kstorage.util.asCachedMutableKrate
import ru.astrainteractive.klibs.kstorage.util.withDefault
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class MoneyAdvancementModule(
    coreModule: CoreModule,
    bukkitCoreModule: BukkitCoreModule
) : Logger by JUtiltLogger("MoneyAdvancementModule") {
    private val mAdvConfigKrate = coreModule.yamlFormat
        .krateOf<MoneyAdvancementsConfiguration>(coreModule.dataFolder.resolve("money_advancements.yml"))
        .withDefault(::MoneyAdvancementsConfiguration)
        .asCachedMutableKrate()

    private val moneyAdvancementEvent = MoneyAdvancementEvent(
        currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory,
        kyoriKrate = coreModule.kyoriKrate,
        translationKrate = coreModule.translationKrate,
        mAdvConfigKrate = mAdvConfigKrate,
    )

    val lifecycle: Lifecycle by lazy {
        Lifecycle.Lambda(
            onEnable = {
                moneyAdvancementEvent.onEnable(bukkitCoreModule.plugin)
            },
            onDisable = {
                moneyAdvancementEvent.onDisable()
            },
            onReload = {
                mAdvConfigKrate.getValue()
            }
        )
    }
}
