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
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPistonEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
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
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.module.adminprivate.util.claimChunk
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

@Suppress("TooManyFunctions")
internal class BukkitClaimEvent(
    dependencies: AdminPrivateDependencies,
) : AdminPrivateDependencies by dependencies, EventListener {
    private val debounce = EventDebounce<RetractKey>(5000L)
    private fun <T> handleDefault(
        retractKey: RetractKey,
        e: T,
        claimChunk: ClaimChunk,
        player: Player?,
        flag: ChunkFlag
    ) where T : Event, T : Cancellable {
        if (player?.toPermissible()?.hasPermission(PluginPermission.AdminClaim) == true) return
        if (e.isCancelled) return
        val sharedEvent = BukkitSharedCancellableEvent(e)
        debounce.debounceEvent(retractKey, sharedEvent) {
            val isAble = adminPrivateController.isAble(claimChunk, flag)
            val isCancelled = !isAble
            if (isCancelled) {
                translation.adminPrivate.actionIsBlockByAdminClaim(flag.name)
                    .let(kyoriComponentSerializer::toComponent)
                    .run { player?.sendMessage(this) }
            }
            isCancelled
        }
    }

    @EventHandler
    fun blockBreakEvent(e: BlockBreakEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.chunk, e.player, "blockBreakEvent"),
            e = e,
            claimChunk = e.block.chunk.claimChunk,
            player = e.player,
            flag = ChunkFlag.BREAK
        )
    }

    @EventHandler
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.chunk, e.player, "blockPlaceEvent"),
            e = e,
            claimChunk = e.blockPlaced.chunk.claimChunk,
            player = e.player,
            flag = ChunkFlag.PLACE
        )
    }

    @EventHandler
    fun interactEvent(e: PlayerInteractEvent) {
        val location = e.interactionPoint ?: e.player.location
        if (e.action == Action.LEFT_CLICK_AIR) return
        if (e.action == Action.RIGHT_CLICK_AIR) return
        handleDefault(
            retractKey = RetractKey.Vararg(location, e.player, "interactEvent"),
            e = e,
            claimChunk = location.chunk.claimChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }

    @EventHandler
    fun itemFrameEvent(e: PlayerItemFrameChangeEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "itemFrameEvent"),
            e = e,
            claimChunk = e.player.location.chunk.claimChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }

    @EventHandler
    fun breakItemFrameEvent(e: HangingBreakByEntityEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location, e.entity, "breakItemFrameEvent"),
            e = e,
            claimChunk = e.entity.location.chunk.claimChunk,
            player = e.remover as? Player,
            flag = ChunkFlag.INTERACT
        )
    }

    @EventHandler
    fun armorStandEvent(e: PlayerArmorStandManipulateEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "armorStandEvent"),
            e = e,
            claimChunk = e.player.location.chunk.claimChunk,
            player = e.player,
            flag = ChunkFlag.INTERACT
        )
    }

    @EventHandler
    fun armorStandBreakEvent(e: EntityDamageByEntityEvent) {
        val armorStand = e.entity as? ArmorStand ?: return
        handleDefault(
            retractKey = RetractKey.Vararg(armorStand.location, armorStand, "armorStandBreakEvent"),
            e = e,
            claimChunk = armorStand.location.chunk.claimChunk,
            player = e.damager as? Player,
            flag = ChunkFlag.INTERACT
        )
    }

    // Explosions
    @EventHandler
    fun onBlockExplode(e: BlockExplodeEvent) {
        e.blockList().forEach { block ->
            handleDefault(
                retractKey = RetractKey.Vararg(block.chunk, "onBlockExplode"),
                e = e,
                claimChunk = block.chunk.claimChunk,
                player = null,
                flag = ChunkFlag.EXPLODE
            )
        }
    }

    @EventHandler
    fun onEntityExplode(e: EntityExplodeEvent) {
        e.blockList().forEach { block ->
            handleDefault(
                retractKey = RetractKey.Vararg(block.chunk, "onEntityExplode"),
                e = e,
                claimChunk = block.chunk.claimChunk,
                player = null,
                flag = ChunkFlag.EXPLODE
            )
        }
    }

    @EventHandler
    fun onPrimeExplosion(e: ExplosionPrimeEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, "onPrimeExplosion"),
            e = e,
            claimChunk = e.entity.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.EXPLODE
        )
    }

    // Placing
    @EventHandler
    fun onBucketEmptyEvent(e: PlayerBucketEmptyEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockClicked.location.chunk, "onBucketEmptyEvent"),
            e = e,
            claimChunk = e.blockClicked.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.EMPTY_BUCKET
        )
    }

    @EventHandler
    fun onTntLavaPlace(e: BlockPlaceEvent) {
        if (!listOf(Material.TNT, Material.LAVA, Material.LAVA_BUCKET).contains(e.blockPlaced.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockPlaced.location.chunk, "onTntLavaPlace"),
            e = e,
            claimChunk = e.blockPlaced.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.EXPLODE
        )
    }

    @EventHandler
    fun onBlockFromTo(e: BlockFromToEvent) {
        if (!listOf(Material.LAVA, Material.LAVA_BUCKET).contains(e.block.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockFromTo"),
            e = e,
            claimChunk = e.block.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }

    @EventHandler
    fun onBlockIgniteEvent(e: BlockIgniteEvent) {
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockIgniteEvent"),
            e = e,
            claimChunk = location.chunk.claimChunk,
            player = e.player,
            flag = ChunkFlag.SPREAD
        )
    }

    @EventHandler
    fun onBlockBurnEvent(e: BlockBurnEvent) {
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockBurnEvent"),
            e = e,
            claimChunk = location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }

    @EventHandler
    fun onBlockSpread(e: BlockSpreadEvent) {
        if (!listOf(Material.LAVA, Material.FIRE).contains(e.block.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockSpread"),
            e = e,
            claimChunk = e.block.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.SPREAD
        )
    }

    @EventHandler
    fun playerDamageEvent(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, player, "playerDamageEvent"),
            e = e,
            claimChunk = player.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.RECEIVE_DAMAGE
        )
    }

    @EventHandler
    fun entitySpawnEvent(e: EntitySpawnEvent) {
        if (e.entity !is Monster || e.entity !is Enemy) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, e.entity, "entitySpawnEvent"),
            e = e,
            claimChunk = e.entity.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.HOSTILE_MOB_SPAWN
        )
    }

    @EventHandler
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
                claimChunk = chunk.claimChunk,
                player = null,
                flag = ChunkFlag.PLACE
            )
        }
    }

    private fun pistonEvent(e: BlockPistonEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "BlockPistonEvent"),
            e = e,
            claimChunk = e.block.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.PLACE
        )
    }

    @EventHandler
    fun onBlockPistonExtendEvent(e: BlockPistonExtendEvent) {
        pistonEvent(e)
    }

    @EventHandler
    fun onBlockPistonRetractEvent(e: BlockPistonRetractEvent) {
        pistonEvent(e)
    }

    @EventHandler
    fun onBlockPistonRetractEvent(e: BlockFadeEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "BlockFadeEvent"),
            e = e,
            claimChunk = e.block.location.chunk.claimChunk,
            player = null,
            flag = ChunkFlag.ICE_MELT
        )
    }
}
