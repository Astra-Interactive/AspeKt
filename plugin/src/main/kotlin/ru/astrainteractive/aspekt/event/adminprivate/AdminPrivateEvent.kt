package ru.astrainteractive.aspekt.event.adminprivate

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerInteractEvent
import ru.astrainteractive.aspekt.adminprivate.debounce.EventDebounce
import ru.astrainteractive.aspekt.adminprivate.debounce.RetractKey
import ru.astrainteractive.aspekt.adminprivate.models.AdminChunk
import ru.astrainteractive.aspekt.adminprivate.models.ChunkFlag
import ru.astrainteractive.aspekt.adminprivate.util.adminChunk
import ru.astrainteractive.aspekt.event.di.EventsModule
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.events.DSLEvent

class AdminPrivateEvent(
    module: EventsModule
) : EventsModule by module {
    private val debounce = EventDebounce<RetractKey>(5000L)
    private fun <T> handleDefault(
        retractKey: RetractKey,
        e: T,
        adminChunk: AdminChunk,
        player: Player?,
        flag: ChunkFlag
    ) where T : Event, T : Cancellable {
        if (player?.let(PluginPermission.AdminClaim::hasPermission) == true) return

        debounce.debounceEvent(retractKey, e) {
            val isAble = adminPrivateController.isAble(adminChunk, flag)
            val isCancelled = !isAble
            if (isCancelled) player?.sendMessage(translation.actionIsBlockByAdminClaim(flag.name))
            isCancelled
        }
    }

    val blockBreakEvent = DSLEvent<BlockBreakEvent>(eventListener, plugin) { e ->
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.chunk, e.player, "blockBreakEvent"),
            e = e,
            adminChunk = e.block.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.BREAK
        )
    }
    val blockPlaceEvent = DSLEvent<BlockPlaceEvent>(eventListener, plugin) { e ->
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.chunk, e.player, "blockPlaceEvent"),
            e = e,
            adminChunk = e.blockPlaced.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.PLACE
        )
    }
    val interactEvent = DSLEvent<PlayerInteractEvent>(eventListener, plugin) { e ->
        val location = e.interactionPoint ?: e.player.location
        handleDefault(
            retractKey = RetractKey.Vararg(location, e.player, "interactEvent"),
            e = e,
            adminChunk = location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }

    // Explosions
    val onBlockExplode = DSLEvent<BlockExplodeEvent>(eventListener, plugin) { e ->
        e.blockList().forEach { block ->
            handleDefault(
                retractKey = RetractKey.Vararg(block.chunk, "onBlockExplode"),
                e = e,
                adminChunk = block.chunk.adminChunk,
                player = null,
                flag = ChunkFlag.EXPLODE
            )
        }
    }
    val onEntityExplode = DSLEvent<BlockExplodeEvent>(eventListener, plugin) { e ->
        e.blockList().forEach { block ->
            handleDefault(
                retractKey = RetractKey.Vararg(block.chunk, "onEntityExplode"),
                e = e,
                adminChunk = block.chunk.adminChunk,
                player = null,
                flag = ChunkFlag.EXPLODE
            )
        }
    }
    val onPrimeExplosion = DSLEvent<ExplosionPrimeEvent>(eventListener, plugin) { e ->
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, "onPrimeExplosion"),
            e = e,
            adminChunk = e.entity.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.EXPLODE
        )
    }

    // Placing
    val onBucketEmptyEvent = DSLEvent<PlayerBucketEmptyEvent>(eventListener, plugin) { e ->
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockClicked.location.chunk, "onBucketEmptyEvent"),
            e = e,
            adminChunk = e.blockClicked.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.EMPTY_BUCKET
        )
    }
    val onTntLavaPlace = DSLEvent<BlockPlaceEvent>(eventListener, plugin) { e ->
        if (!listOf(Material.TNT, Material.LAVA, Material.LAVA_BUCKET).contains(e.blockPlaced.type)) return@DSLEvent
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockPlaced.location.chunk, "onTntLavaPlace"),
            e = e,
            adminChunk = e.blockPlaced.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.EXPLODE
        )
    }
    val onBlockFromTo = DSLEvent<BlockFromToEvent>(eventListener, plugin) { e ->
        if (!listOf(Material.LAVA, Material.LAVA_BUCKET).contains(e.block.type)) return@DSLEvent
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockFromTo"),
            e = e,
            adminChunk = e.block.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }
    val onBlockIgniteEvent = DSLEvent<BlockIgniteEvent>(eventListener, plugin) { e ->
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockIgniteEvent"),
            e = e,
            adminChunk = location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.SPREAD
        )
    }
    val onBlockBurnEvent = DSLEvent<BlockBurnEvent>(eventListener, plugin) { e ->
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockBurnEvent"),
            e = e,
            adminChunk = location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }
    val onBlockSpread = DSLEvent<BlockSpreadEvent>(eventListener, plugin) { e ->
        if (!listOf(Material.LAVA, Material.FIRE).contains(e.block.type)) return@DSLEvent
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockSpread"),
            e = e,
            adminChunk = e.block.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }
}
