package ru.astrainteractive.aspekt.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.gui.entities.ui.EntitiesGui
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.string.BukkitTranslationContext

class RouterImpl(
    private val scope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    private val translationContext: BukkitTranslationContext,
) : Router {
    override fun open(route: Router.Route) {
        scope.launch(dispatchers.BukkitAsync) {
            val gui = when (route) {
                is Router.Route.Entities -> EntitiesGui(
                    player = route.player,
                    bukkitDispatchers = dispatchers,
                    translationContext = translationContext
                )
            }
            withContext(dispatchers.BukkitMain) {
                gui.open()
            }
        }
    }
}
