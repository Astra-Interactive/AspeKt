package ru.astrainteractive.aspekt.command.di

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.gui.di.GuiModule
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface CommandsDependencies {
    val plugin: JavaPlugin
    val translation: PluginTranslation
    val dispatchers: BukkitDispatchers
    val scope: AsyncComponent
    val sitController: SitController
    val economyProvider: EconomyProvider?
    val router: Router
    val kyoriComponentSerializer: KyoriComponentSerializer

    class Default(
        coreModule: CoreModule,
        eventsModule: EventsModule,
        guiModule: GuiModule,
    ) : CommandsDependencies {

        override val plugin: JavaPlugin by coreModule.plugin
        override val translation: PluginTranslation by coreModule.translation
        override val dispatchers: BukkitDispatchers = coreModule.dispatchers
        override val scope: AsyncComponent = coreModule.scope
        override val sitController: SitController by Provider { eventsModule.sitModule.sitController }
        override val economyProvider: EconomyProvider? by coreModule.economyProvider
        override val router: Router by Provider {
            guiModule.router
        }
        override val kyoriComponentSerializer by coreModule.kyoriComponentSerializer
    }
}
