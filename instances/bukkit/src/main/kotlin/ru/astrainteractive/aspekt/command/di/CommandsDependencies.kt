package ru.astrainteractive.aspekt.command.di

import kotlinx.coroutines.CoroutineScope
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.lifecycle.LifecyclePlugin
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

interface CommandsDependencies {
    val plugin: LifecyclePlugin
    val translation: PluginTranslation
    val dispatchers: KotlinDispatchers
    val scope: CoroutineScope
    val economyProvider: EconomyFacade?
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
        bukkitCoreModule: BukkitCoreModule
    ) : CommandsDependencies {
        override val plugin = bukkitCoreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val dispatchers: KotlinDispatchers = coreModule.dispatchers
        override val scope: CoroutineScope = coreModule.scope
        override val economyProvider: EconomyFacade? = bukkitCoreModule.currencyEconomyProviderFactory.findDefault()
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
