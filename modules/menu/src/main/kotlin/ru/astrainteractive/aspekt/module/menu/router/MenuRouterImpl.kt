package ru.astrainteractive.aspekt.module.menu.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.gui.MenuGui
import ru.astrainteractive.aspekt.module.menu.model.MenuModel

internal class MenuRouterImpl(private val coreModule: CoreModule) : MenuRouter {
    override fun openMenu(player: Player, menuModel: MenuModel) {
        coreModule.scope.launch(coreModule.dispatchers.IO) {
            val gui = MenuGui(
                player = player,
                menuModel = menuModel,
                translation = coreModule.translation.cachedValue,
                dispatchers = coreModule.dispatchers,
                kyoriComponentSerializer = coreModule.kyoriComponentSerializer.cachedValue,
                economyProvider = coreModule.currencyEconomyProviderFactory.findDefault()
            )
            withContext(coreModule.dispatchers.Main) {
                gui.open()
            }
        }
    }
}
