package ru.astrainteractive.aspekt.module.adminprivate.event

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Enemy
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerInteractEvent
import ru.astrainteractive.aspekt.module.adminprivate.debounce.EventDebounce
import ru.astrainteractive.aspekt.module.adminprivate.debounce.RetractKey
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.module.adminprivate.util.adminChunk
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.event.DSLEvent
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext

class AdminPrivateEvent(
    module: AdminPrivateDependencies,
) : AdminPrivateDependencies by module,
    BukkitTranslationContext by module.translationContext {
    private val debounce = EventDebounce<RetractKey>(5000L)
    private fun <T> handleDefault(
        retractKey: RetractKey,
        e: T,
        adminChunk: AdminChunk,
        player: Player?,
        flag: ChunkFlag
    ) where T : Event, T : Cancellable {
        if (!adminPrivateController.isEnabled) return

        if (player?.toPermissible()?.hasPermission(PluginPermission.AdminClaim) == true) return
        debounce.debounceEvent(retractKey, e) {
            val isAble = adminPrivateController.isAble(adminChunk, flag)
            val isCancelled = !isAble
            if (isCancelled) player?.sendMessage(translation.adminPrivate.actionIsBlockByAdminClaim(flag.name))
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
        if (e.action == Action.LEFT_CLICK_AIR) return@DSLEvent
        if (e.action == Action.RIGHT_CLICK_AIR) return@DSLEvent
        handleDefault(
            retractKey = RetractKey.Vararg(location, e.player, "interactEvent"),
            e = e,
            adminChunk = location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }
    val itemFrameEvent = DSLEvent<PlayerItemFrameChangeEvent>(eventListener, plugin) { e ->
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "itemFrameEvent"),
            e = e,
            adminChunk = e.player.location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }
    val breakItemFrameEvent = DSLEvent<HangingBreakByEntityEvent>(eventListener, plugin) { e ->
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location, e.entity, "breakItemFrameEvent"),
            e = e,
            adminChunk = e.entity.location.chunk.adminChunk,
            player = e.remover as? Player,
            flag = ChunkFlag.INTERACT
        )
    }
    val armorStandEvent = DSLEvent<PlayerArmorStandManipulateEvent>(eventListener, plugin) { e ->
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "armorStandEvent"),
            e = e,
            adminChunk = e.player.location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }
    val armorStandBreakEvent = DSLEvent<EntityDamageByEntityEvent>(eventListener, plugin) { e ->
        val armorStand = e.entity as? ArmorStand ?: return@DSLEvent
        handleDefault(
            retractKey = RetractKey.Vararg(armorStand.location, armorStand, "armorStandBreakEvent"),
            e = e,
            adminChunk = armorStand.location.chunk.adminChunk,
            player = e.damager as? Player,
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
    val playerDamageEvent = DSLEvent<EntityDamageEvent>(eventListener, plugin) { e ->
        val player = e.entity as? Player ?: return@DSLEvent
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, player, "playerDamageEvent"),
            e = e,
            adminChunk = player.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.RECEIVE_DAMAGE
        )
    }
    val entitySpawnEvent = DSLEvent<EntitySpawnEvent>(eventListener, plugin) { e ->
        if (e.entity !is Monster || e.entity !is Enemy) return@DSLEvent
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, e.entity, "entitySpawnEvent"),
            e = e,
            adminChunk = e.entity.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.HOSTILE_MOB_SPAWN
        )
    }
}
