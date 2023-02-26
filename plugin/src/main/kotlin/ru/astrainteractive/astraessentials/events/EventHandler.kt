package ru.astrainteractive.astraessentials.events

import ru.astrainteractive.astraessentials.events.sit.SitEvent
import ru.astrainteractive.astraessentials.events.sort.SortEvent
import ru.astrainteractive.astraessentials.modules.ServiceLocator


/**
 * Handler for all your events
 */
class EventHandler(controllers: ServiceLocator.Controllers) {

    init {
        SitEvent(
            sitControllerDependency = controllers.sitController
        )
        SortEvent(
            sortControllerDependency = controllers.sortController
        )
        AutoCrop()
    }
}
