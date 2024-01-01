package ru.astrainteractive.aspekt.module.menu.router

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.gui.MenuGui
import ru.astrainteractive.aspekt.module.menu.model.MenuModel

internal class MenuRouterImpl(private val coreModule: CoreModule) : MenuRouter {
    override fun openMenu(player: Player, menuModel: MenuModel) {
        coreModule.scope.value.launch(coreModule.dispatchers.value.IO) {
            val gui = MenuGui(
                player = player,
                menuModel = menuModel,
                translation = coreModule.translation.value,
                dispatchers = coreModule.dispatchers.value,
                translationContext = coreModule.translationContext,
                economyProvider = coreModule.economyProvider.value
            )
            withContext(coreModule.dispatchers.value.Main) {
                gui.open()
            }
        }
    }
}
