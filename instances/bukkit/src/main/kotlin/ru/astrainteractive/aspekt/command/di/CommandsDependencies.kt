package ru.astrainteractive.aspekt.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin

interface CommandsDependencies {
    val plugin: LifecyclePlugin
    val translation: PluginTranslation
    val dispatchers: BukkitDispatchers
    val scope: CoroutineScope
    val economyProvider: EconomyFacade?
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
    ) : CommandsDependencies {
        override val plugin = coreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val dispatchers: BukkitDispatchers = coreModule.dispatchers
        override val scope: CoroutineScope = coreModule.scope
        override val economyProvider: EconomyFacade? = coreModule.currencyEconomyProviderFactory.findDefault()
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
