package ru.astrainteractive.aspekt.module.entities.gui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.entities.gui.entities.ui.EntitiesGui
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate

internal class RouterImpl(
    private val scope: CoroutineScope,
    private val dispatchers: BukkitDispatchers,
    private val kyoriComponentSerializerKrate: Krate<KyoriComponentSerializer>,
) : Router {
    override fun open(route: Router.Route) {
        scope.launch(dispatchers.BukkitAsync) {
            val gui = when (route) {
                is Router.Route.Entities -> EntitiesGui(
                    player = route.player,
                    bukkitDispatchers = dispatchers,
                    kyoriComponentSerializerKrate = kyoriComponentSerializerKrate
                )
            }
            withContext(dispatchers.BukkitMain) {
                gui.open()
            }
        }
    }
}
