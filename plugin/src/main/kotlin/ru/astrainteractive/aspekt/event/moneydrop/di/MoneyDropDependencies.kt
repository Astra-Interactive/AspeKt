package ru.astrainteractive.aspekt.event.moneydrop.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.moneydrop.MoneyDropController
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.klibs.kdi.getValue

interface MoneyDropDependencies {
    val eventListener: EventListener
    val plugin: JavaPlugin
    val configuration: PluginConfiguration
    val moneyDropController: MoneyDropController
    val economyProvider: EconomyProvider?
    val translationContext: BukkitTranslationContext
    val translation: PluginTranslation

    class Default(
        coreModule: CoreModule,
        override val moneyDropController: MoneyDropController
    ) : MoneyDropDependencies {
        override val eventListener: EventListener by coreModule.eventListener
        override val plugin: JavaPlugin by coreModule.plugin
        override val configuration: PluginConfiguration by coreModule.pluginConfig
        override val economyProvider: EconomyProvider? by coreModule.economyProvider
        override val translationContext: BukkitTranslationContext = coreModule.translationContext
        override val translation: PluginTranslation by coreModule.translation
    }
}
