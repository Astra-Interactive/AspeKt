package ru.astrainteractive.aspekt.module.moneydrop.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropController
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer

internal interface MoneyDropDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val moneyDropController: MoneyDropController
    val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory
    val kyoriComponentSerializer: KyoriComponentSerializer
    val translation: PluginTranslation
    val scope: CoroutineScope

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule,
        override val moneyDropController: MoneyDropController
    ) : MoneyDropDependencies {
        override val eventListener: EventListener = bukkitCoreModule.eventListener
        override val plugin: JavaPlugin = bukkitCoreModule.plugin
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
        override val translation: PluginTranslation by coreModule.translation
        override val currencyEconomyProviderFactory = bukkitCoreModule.currencyEconomyProviderFactory
        override val scope: CoroutineScope = coreModule.scope
    }
}
