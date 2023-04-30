package ru.astrainteractive.aspekt.events

import ru.astrainteractive.aspekt.events.crop.AutoCropEvent
import ru.astrainteractive.aspekt.events.di.EventsModule
import ru.astrainteractive.aspekt.events.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.events.sit.SitEvent
import ru.astrainteractive.aspekt.events.sort.SortEvent
import ru.astrainteractive.aspekt.events.tc.TCEvent

/**
 * Handler for all your events
 */
class EventHandler(
    module: EventsModule
) {

    init {
        SitEvent(module)
        SortEvent(module)
        AutoCropEvent(module)
        TCEvent(module)
        RestrictionsEvent(module)
    }
}
