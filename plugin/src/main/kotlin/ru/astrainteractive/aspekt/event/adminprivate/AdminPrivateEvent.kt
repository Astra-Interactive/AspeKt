package ru.astrainteractive.aspekt.event.adminprivate

import org.bukkit.event.block.BlockPlaceEvent
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.astralibs.events.DSLEvent

class AdminPrivateEvent(
    module: EventsModule
) : EventsModule by module {
    val onBlockPlaced = DSLEvent<BlockPlaceEvent>(eventListener, plugin) { e ->
    }
}
