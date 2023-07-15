package ru.astrainteractive.aspekt.event.adminprivate

import org.bukkit.event.block.BlockBreakEvent
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

    val blockBreakEvent = DSLEvent<BlockBreakEvent>(eventListener, plugin) { e ->
        val retractKey = RetractKey.Vararg(e.block.chunk, e.player)
        debounce.getOrNull(retractKey, e) {
            val isAble = adminPrivateController.isAble(e.block.chunk.adminChunk, ChunkFlag.BREAK)
            val isCancelled = !isAble
            isCancelled
        }
    }
}
