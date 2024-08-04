package ru.astrainteractive.aspekt.module.moneydrop.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.moneydrop.MoneyDropController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kdi.getValue

interface MoneyDropDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration
    val moneyDropController: MoneyDropController
    val economyProvider: EconomyProvider?
    val kyoriComponentSerializer: KyoriComponentSerializer
    val translation: PluginTranslation

    class Default(
        coreModule: CoreModule,
        override val moneyDropController: MoneyDropController
    ) : MoneyDropDependencies {
        override val eventListener: EventListener = coreModule.eventListener
        override val plugin: JavaPlugin by coreModule.plugin
        override val configuration: PluginConfiguration by coreModule.pluginConfig
        override val economyProvider: EconomyProvider? by coreModule.economyProvider
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
        override val translation: PluginTranslation by coreModule.translation
    }
}
