package ru.astrainteractive.aspekt.module.menu.di

import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.di.CoreModule
import ru.astrainteractive.aspekt.module.menu.di.factory.MenuModelsFactory
import ru.astrainteractive.aspekt.module.menu.gui.MenuGui
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.aspekt.util.Lifecycle
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.klibs.kdi.Reloadable

interface MenuModule : Lifecycle {
    val menuModels: Reloadable<List<MenuModel>>
    fun menuGui(player: Player, menuModel: MenuModel): MenuGui

    class Default(
        private val coreModule: CoreModule
    ) : MenuModule {
        override val menuModels: Reloadable<List<MenuModel>> = Reloadable {
            MenuModelsFactory(coreModule.plugin.value.dataFolder, YamlSerializer()).create()
        }

        override fun menuGui(
            player: Player,
            menuModel: MenuModel
        ): MenuGui {
            return MenuGui(
                player = player,
                menuModel = menuModel,
                translation = coreModule.translation.value,
                dispatchers = coreModule.dispatchers.value,
                translationContext = coreModule.translationContext,
                economyProvider = coreModule.economyProvider.value
            )
        }

        override fun onReload() {
            menuModels.reload()
        }
    }
}
