package ru.astrainteractive.aspekt.command.di

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.entities.command.EntitiesCommandDependencies
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.event.sit.di.SitModule
import ru.astrainteractive.aspekt.module.entities.gui.Router
import ru.astrainteractive.aspekt.module.entities.gui.di.GuiModule
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer

interface CommandsDependencies: EntitiesCommandDependencies {
    override val plugin: JavaPlugin
    val translation: PluginTranslation
    val dispatchers: BukkitDispatchers
    val scope: CoroutineScope
    val sitController: SitController
    val economyProvider: EconomyFacade?
    override val router: Router
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
        private val sitModule: SitModule,
        private val guiModule: GuiModule,
    ) : CommandsDependencies {

        override val plugin: JavaPlugin = coreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val dispatchers: BukkitDispatchers = coreModule.dispatchers
        override val scope: CoroutineScope = coreModule.scope
        override val sitController: SitController
            get() = sitModule.sitController
        override val economyProvider: EconomyFacade? = coreModule.currencyEconomyProviderFactory.findDefault()
        override val router: Router
            get() = guiModule.router
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
