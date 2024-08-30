package ru.astrainteractive.aspekt.module.adminprivate.event

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockState
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
import org.bukkit.event.block.BlockPistonEvent
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
import org.bukkit.event.world.PortalCreateEvent
import ru.astrainteractive.aspekt.module.adminprivate.debounce.EventDebounce
import ru.astrainteractive.aspekt.module.adminprivate.debounce.RetractKey
import ru.astrainteractive.aspekt.module.adminprivate.event.di.AdminPrivateDependencies
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.module.adminprivate.util.adminChunk
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

@Suppress("TooManyFunctions")
internal class AdminPrivateEvent(
    dependencies: AdminPrivateDependencies,
) : AdminPrivateDependencies by dependencies, EventListener {
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
        if (e.isCancelled) return

        debounce.debounceEvent(retractKey, e) {
            val isAble = adminPrivateController.isAble(adminChunk, flag)
            val isCancelled = !isAble
            if (isCancelled) {
                translation.adminPrivate.actionIsBlockByAdminClaim(flag.name)
                    .let(kyoriComponentSerializer::toComponent)
                    .run { player?.sendMessage(this) }
            }
            isCancelled
        }
    }

    fun blockBreakEvent(e: BlockBreakEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.chunk, e.player, "blockBreakEvent"),
            e = e,
            adminChunk = e.block.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.BREAK
        )
    }

    fun blockPlaceEvent(e: BlockPlaceEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.chunk, e.player, "blockPlaceEvent"),
            e = e,
            adminChunk = e.blockPlaced.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.PLACE
        )
    }

    fun interactEvent(e: PlayerInteractEvent) {
        val location = e.interactionPoint ?: e.player.location
        if (e.action == Action.LEFT_CLICK_AIR) return
        if (e.action == Action.RIGHT_CLICK_AIR) return
        handleDefault(
            retractKey = RetractKey.Vararg(location, e.player, "interactEvent"),
            e = e,
            adminChunk = location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }

    fun itemFrameEvent(e: PlayerItemFrameChangeEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "itemFrameEvent"),
            e = e,
            adminChunk = e.player.location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }

    fun breakItemFrameEvent(e: HangingBreakByEntityEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location, e.entity, "breakItemFrameEvent"),
            e = e,
            adminChunk = e.entity.location.chunk.adminChunk,
            player = e.remover as? Player,
            flag = ChunkFlag.INTERACT
        )
    }

    fun armorStandEvent(e: PlayerArmorStandManipulateEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "armorStandEvent"),
            e = e,
            adminChunk = e.player.location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }

    fun armorStandBreakEvent(e: EntityDamageByEntityEvent) {
        val armorStand = e.entity as? ArmorStand ?: return
        handleDefault(
            retractKey = RetractKey.Vararg(armorStand.location, armorStand, "armorStandBreakEvent"),
            e = e,
            adminChunk = armorStand.location.chunk.adminChunk,
            player = e.damager as? Player,
            flag = ChunkFlag.INTERACT
        )
    }

    // Explosions
    fun onBlockExplode(e: BlockExplodeEvent) {
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

    fun onEntityExplode(e: BlockExplodeEvent) {
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

    fun onPrimeExplosion(e: ExplosionPrimeEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, "onPrimeExplosion"),
            e = e,
            adminChunk = e.entity.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.EXPLODE
        )
    }

    // Placing
    fun onBucketEmptyEvent(e: PlayerBucketEmptyEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockClicked.location.chunk, "onBucketEmptyEvent"),
            e = e,
            adminChunk = e.blockClicked.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.EMPTY_BUCKET
        )
    }

    fun onTntLavaPlace(e: BlockPlaceEvent) {
        if (!listOf(Material.TNT, Material.LAVA, Material.LAVA_BUCKET).contains(e.blockPlaced.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockPlaced.location.chunk, "onTntLavaPlace"),
            e = e,
            adminChunk = e.blockPlaced.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.EXPLODE
        )
    }

    fun onBlockFromTo(e: BlockFromToEvent) {
        if (!listOf(Material.LAVA, Material.LAVA_BUCKET).contains(e.block.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockFromTo"),
            e = e,
            adminChunk = e.block.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }

    fun onBlockIgniteEvent(e: BlockIgniteEvent) {
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockIgniteEvent"),
            e = e,
            adminChunk = location.chunk.adminChunk,
            player = e.player,
            flag = ChunkFlag.SPREAD
        )
    }

    fun onBlockBurnEvent(e: BlockBurnEvent) {
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockBurnEvent"),
            e = e,
            adminChunk = location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }

    fun onBlockSpread(e: BlockSpreadEvent) {
        if (!listOf(Material.LAVA, Material.FIRE).contains(e.block.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockSpread"),
            e = e,
            adminChunk = e.block.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }

    fun playerDamageEvent(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, player, "playerDamageEvent"),
            e = e,
            adminChunk = player.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.RECEIVE_DAMAGE
        )
    }

    fun entitySpawnEvent(e: EntitySpawnEvent) {
        if (e.entity !is Monster || e.entity !is Enemy) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, e.entity, "entitySpawnEvent"),
            e = e,
            adminChunk = e.entity.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.HOSTILE_MOB_SPAWN
        )
    }

    fun portalCreateEvent(e: PortalCreateEvent) {
        val chunks = e.blocks
            .map(BlockState::getLocation)
            .distinctBy(Location::getChunk)
            .map(Location::getChunk)
        chunks.forEach { chunk ->
            handleDefault(
                retractKey = RetractKey.Vararg(
                    chunks,
                    "portalCreateEvent"
                ),
                e = e,
                adminChunk = chunk.adminChunk,
                player = null,
                flag = ChunkFlag.PLACE
            )
        }
    }

    fun pistonEvent(e: BlockPistonEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "BlockPistonEvent"),
            e = e,
            adminChunk = e.block.location.chunk.adminChunk,
            player = null,
            flag = ChunkFlag.PLACE
        )
    }
}
