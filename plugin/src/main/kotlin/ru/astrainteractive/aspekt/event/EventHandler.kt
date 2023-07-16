package ru.astrainteractive.aspekt.event

import ru.astrainteractive.aspekt.event.adminprivate.AdminPrivateEvent
import ru.astrainteractive.aspekt.event.crop.AutoCropEvent
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.event.restrictions.RestrictionsEvent
import ru.astrainteractive.aspekt.event.sit.SitEvent
import ru.astrainteractive.aspekt.event.sort.SortEvent
import ru.astrainteractive.aspekt.event.tc.TCEvent

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
        AdminPrivateEvent(module)
    }
}
