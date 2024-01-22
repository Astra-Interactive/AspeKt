package ru.astrainteractive.aspekt.event

import ru.astrainteractive.aspekt.event.di.EventsModule

/**
 * Handler for all your events
 */
class EventHandler(
    module: EventsModule
) {
    init {
        module.tcEvent
        module.sortEvent
        module.sitModule.sitEvent
        module.restrictionsEvent
    }
}
