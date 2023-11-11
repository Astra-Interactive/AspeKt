package ru.astrainteractive.aspekt.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.gui.entities.EntitiesGui
import ru.astrainteractive.aspekt.gui.menu.MenuGui
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.economy.EconomyProvider
import ru.astrainteractive.astralibs.string.BukkitTranslationContext

class RouterImpl(
    private val scope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    private val translationContext: BukkitTranslationContext,
    private val economyProvider: EconomyProvider?,
    private val translation: PluginTranslation
) : Router {
    override fun open(route: Router.Route) {
        scope.launch(dispatchers.BukkitAsync) {
            val gui = when (route) {
                is Router.Route.Entities -> EntitiesGui(
                    player = route.player,
                    bukkitDispatchers = dispatchers,
                    translationContext = translationContext
                )

                is Router.Route.Menu -> MenuGui(
                    player = route.player,
                    economyProvider = economyProvider,
                    translation = translation,
                    translationContext = translationContext,
                    dispatchers = dispatchers,
                    menuModel = route.menuModel
                )
            }
            withContext(dispatchers.BukkitMain) {
                gui.open()
            }
        }
    }
}
