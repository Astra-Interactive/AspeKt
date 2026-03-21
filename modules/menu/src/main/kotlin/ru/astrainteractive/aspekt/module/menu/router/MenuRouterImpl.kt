package ru.astrainteractive.aspekt.module.menu.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.di.BukkitCoreModule
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.gui.MenuGui
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.astralibs.server.player.BukkitOnlineKPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.util.cast

internal class MenuRouterImpl(
    private val coreModule: CoreModule,
    private val bukkitCoreModule: BukkitCoreModule
) : MenuRouter {
    override fun openMenu(player: OnlineKPlayer, menuModel: MenuModel) {
        val bukkitPlayer = player.cast<BukkitOnlineKPlayer>().instance
        coreModule.ioScope.launch(coreModule.dispatchers.IO) {
            val gui = MenuGui(
                player = bukkitPlayer,
                menuModel = menuModel,
                translation = coreModule.translation.cachedValue,
                dispatchers = coreModule.dispatchers,
                kyoriComponentSerializer = coreModule.kyoriKrate.cachedValue,
                economyProvider = bukkitCoreModule.currencyEconomyProviderFactory.findDefault()
            )
            withContext(coreModule.dispatchers.Main) {
                gui.open()
            }
        }
    }
}
