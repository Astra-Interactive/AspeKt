package ru.astrainteractive.aspekt.command.di

import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.aspekt.event.sit.SitController
import ru.astrainteractive.aspekt.gui.Router
import ru.astrainteractive.aspekt.plugin.MenuModel
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.klibs.kdi.Module
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

interface CommandsDependencies : Module {
    val plugin: AspeKt
    val translation: PluginTranslation
    val dispatchers: BukkitDispatchers
    val scope: AsyncComponent
    val sitController: SitController
    val adminPrivateController: AdminPrivateController
    val menuModels: List<MenuModel>
    val economyProvider: EconomyProvider?
    val router: Router
    class Default(
        rootModule: RootModule
    ) : CommandsDependencies {

        override val plugin: AspeKt by rootModule.plugin
        override val translation: PluginTranslation by rootModule.translation
        override val dispatchers: BukkitDispatchers by rootModule.dispatchers
        override val scope: AsyncComponent by rootModule.scope
        override val sitController: SitController by Provider { rootModule.eventsModule.sitModule.sitController }
        override val menuModels: List<MenuModel> by rootModule.menuModels
        override val economyProvider: EconomyProvider? by rootModule.economyProvider
        override val adminPrivateController: AdminPrivateController by Provider {
            rootModule.adminPrivateModule.adminPrivateController
        }
        override val router: Router by rootModule.router
    }
}
