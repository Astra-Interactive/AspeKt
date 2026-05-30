package ru.astrainteractive.aspekt.module.rtp.search

import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.TicketType
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.ChunkStatus
import net.minecraft.world.level.chunk.LevelChunk
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import ru.astrainteractive.klibs.mikro.core.util.tryCast
import java.util.Comparator

internal class ChunkLoader(
    private val dispatchers: KotlinDispatchers,
) : Logger by JUtiltLogger("SafeLocationProvider") {

    suspend fun loadToFull(level: ServerLevel, chunkX: Int, chunkZ: Int): LevelChunk? {
        val chunkPos = ChunkPos(chunkX, chunkZ)
        withContext(dispatchers.Main) {
            level.chunkSource.addRegionTicket(RTP_TICKET, chunkPos, 0, chunkPos)
        }
        return level.chunkSource.getChunkFuture(chunkX, chunkZ, ChunkStatus.FULL, true)
            .await()
            .ifRight { failure ->
                error { "#loadChunkToFull fail: $failure" }
            }
            .left()
            .orElse(null)
            ?.tryCast<LevelChunk>()
    }

    private companion object {
        // 20 ticks per 5 seconds
        private const val RTP_TICKET_TIMEOUT_TICKS = 20 * 5

        private val RTP_TICKET: TicketType<ChunkPos> = TicketType.create(
            "aspekt_rtp",
            Comparator.comparingLong { pos: ChunkPos -> pos.toLong() },
            RTP_TICKET_TIMEOUT_TICKS
        )
    }
}
