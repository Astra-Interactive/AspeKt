package ru.astrainteractive.astraessentials.events

import ru.astrainteractive.astraessentials.events.sit.SitEvent
import ru.astrainteractive.astraessentials.events.sort.SortEvent


/**
 * Handler for all your events
 */
class EventHandler {

    init {
        SitEvent()
        SortEvent()
        AutoCrop()
    }
}
