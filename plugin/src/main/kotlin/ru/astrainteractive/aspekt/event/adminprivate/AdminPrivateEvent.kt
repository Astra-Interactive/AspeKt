package ru.astrainteractive.aspekt.event.adminprivate

import org.bukkit.event.block.BlockPlaceEvent
import ru.astrainteractive.aspekt.adminprivate.debounce.EventDebounce
import ru.astrainteractive.aspekt.adminprivate.debounce.RetractKey
import ru.astrainteractive.aspekt.adminprivate.models.ChunkFlag
import ru.astrainteractive.aspekt.adminprivate.util.adminChunk
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.astralibs.events.DSLEvent

class AdminPrivateEvent(
    module: EventsModule
) : EventsModule by module {
    private val debounce = EventDebounce<RetractKey>(5000L)

    val onBlockPlaced = DSLEvent<BlockPlaceEvent>(eventListener, plugin) { e ->
        val retractKey = RetractKey.Vararg(e.block.chunk, e.player)
        debounce.getOrNull(retractKey, e) {
            val isAble = adminPrivateController.isAble(e.block.chunk.adminChunk, ChunkFlag.BREAK)
            if (isAble) null else false
        }
    }
}
