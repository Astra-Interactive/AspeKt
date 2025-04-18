package ru.astrainteractive.aspekt.module.claims.event

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
import ru.astrainteractive.aspekt.module.claims.data.isAble
import ru.astrainteractive.aspekt.module.claims.debounce.EventDebounce
import ru.astrainteractive.aspekt.module.claims.debounce.RetractKey
import ru.astrainteractive.aspekt.module.claims.event.di.ClaimDependencies
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.asClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.asClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible

@Suppress("TooManyFunctions")
internal class BukkitClaimEvent(
    dependencies: ClaimDependencies,
) : ClaimDependencies by dependencies, EventListener {
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
            val isAble = claimsRepository.isAble(
                key = claimChunk.uniqueWorldKey,
                chunkFlag = flag,
                claimPlayer = player?.asClaimPlayer()
            )
            val isCancelled = !isAble
            if (isCancelled) {
                translation.claim.actionIsBlockByAdminClaim(flag.name)
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
            claimChunk = e.block.chunk.asClaimChunk(),
            player = e.player,
            flag = ChunkFlag.ALLOW_BREAK
        )
    }

    @EventHandler
    fun blockPlaceEvent(e: BlockPlaceEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.chunk, e.player, "blockPlaceEvent"),
            e = e,
            claimChunk = e.blockPlaced.chunk.asClaimChunk(),
            player = e.player,
            flag = ChunkFlag.ALLOW_PLACE
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
            claimChunk = location.chunk.asClaimChunk(),
            player = e.player,
            flag = ChunkFlag.ALLOW_INTERACT
        )
    }

    @EventHandler
    fun itemFrameEvent(e: PlayerItemFrameChangeEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "itemFrameEvent"),
            e = e,
            claimChunk = e.player.location.chunk.asClaimChunk(),
            player = e.player,
            flag = ChunkFlag.ALLOW_INTERACT
        )
    }

    @EventHandler
    fun breakItemFrameEvent(e: HangingBreakByEntityEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location, e.entity, "breakItemFrameEvent"),
            e = e,
            claimChunk = e.entity.location.chunk.asClaimChunk(),
            player = e.remover as? Player,
            flag = ChunkFlag.ALLOW_INTERACT
        )
    }

    @EventHandler
    fun armorStandEvent(e: PlayerArmorStandManipulateEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.player.location, e.player, "armorStandEvent"),
            e = e,
            claimChunk = e.player.location.chunk.asClaimChunk(),
            player = e.player,
            flag = ChunkFlag.ALLOW_INTERACT
        )
    }

    @EventHandler
    fun armorStandBreakEvent(e: EntityDamageByEntityEvent) {
        val armorStand = e.entity as? ArmorStand ?: return
        handleDefault(
            retractKey = RetractKey.Vararg(armorStand.location, armorStand, "armorStandBreakEvent"),
            e = e,
            claimChunk = armorStand.location.chunk.asClaimChunk(),
            player = e.damager as? Player,
            flag = ChunkFlag.ALLOW_INTERACT
        )
    }

    // Explosions
    @EventHandler
    fun onBlockExplode(e: BlockExplodeEvent) {
        e.blockList().forEach { block ->
            handleDefault(
                retractKey = RetractKey.Vararg(block.chunk, "onBlockExplode"),
                e = e,
                claimChunk = block.chunk.asClaimChunk(),
                player = null,
                flag = ChunkFlag.ALLOW_EXPLODE
            )
        }
    }

    @EventHandler
    fun onEntityExplode(e: EntityExplodeEvent) {
        e.blockList().forEach { block ->
            handleDefault(
                retractKey = RetractKey.Vararg(block.chunk, "onEntityExplode"),
                e = e,
                claimChunk = block.chunk.asClaimChunk(),
                player = null,
                flag = ChunkFlag.ALLOW_EXPLODE
            )
        }
    }

    @EventHandler
    fun onPrimeExplosion(e: ExplosionPrimeEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, "onPrimeExplosion"),
            e = e,
            claimChunk = e.entity.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_EXPLODE
        )
    }

    // Placing
    @EventHandler
    fun onBucketEmptyEvent(e: PlayerBucketEmptyEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockClicked.location.chunk, "onBucketEmptyEvent"),
            e = e,
            claimChunk = e.blockClicked.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_EMPTY_BUCKET
        )
    }

    @EventHandler
    fun onTntLavaPlace(e: BlockPlaceEvent) {
        if (!listOf(Material.TNT, Material.LAVA, Material.LAVA_BUCKET).contains(e.blockPlaced.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.blockPlaced.location.chunk, "onTntLavaPlace"),
            e = e,
            claimChunk = e.blockPlaced.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_EXPLODE
        )
    }

    @EventHandler
    fun onBlockFromTo(e: BlockFromToEvent) {
        if (!listOf(Material.LAVA, Material.LAVA_BUCKET).contains(e.block.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockFromTo"),
            e = e,
            claimChunk = e.block.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_SPREAD
        )
    }

    @EventHandler
    fun onBlockIgniteEvent(e: BlockIgniteEvent) {
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockIgniteEvent"),
            e = e,
            claimChunk = location.chunk.asClaimChunk(),
            player = e.player,
            flag = ChunkFlag.ALLOW_SPREAD
        )
    }

    @EventHandler
    fun onBlockBurnEvent(e: BlockBurnEvent) {
        val location = (e.ignitingBlock ?: e.block).location
        handleDefault(
            retractKey = RetractKey.Vararg(location.chunk, "onBlockBurnEvent"),
            e = e,
            claimChunk = location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_SPREAD
        )
    }

    @EventHandler
    fun onBlockSpread(e: BlockSpreadEvent) {
        if (!listOf(Material.LAVA, Material.FIRE).contains(e.block.type)) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "onBlockSpread"),
            e = e,
            claimChunk = e.block.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_SPREAD
        )
    }

    @EventHandler
    fun playerDamageEvent(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, player, "playerDamageEvent"),
            e = e,
            claimChunk = player.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_RECEIVE_DAMAGE
        )
    }

    @EventHandler
    fun entitySpawnEvent(e: EntitySpawnEvent) {
        if (e.entity !is Monster || e.entity !is Enemy) return
        handleDefault(
            retractKey = RetractKey.Vararg(e.entity.location.chunk, e.entity, "entitySpawnEvent"),
            e = e,
            claimChunk = e.entity.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_HOSTILE_MOB_SPAWN
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
                claimChunk = chunk.asClaimChunk(),
                player = null,
                flag = ChunkFlag.ALLOW_PLACE
            )
        }
    }

    private fun pistonEvent(e: BlockPistonEvent) {
        handleDefault(
            retractKey = RetractKey.Vararg(e.block.location.chunk, "BlockPistonEvent"),
            e = e,
            claimChunk = e.block.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_PLACE
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
            claimChunk = e.block.location.chunk.asClaimChunk(),
            player = null,
            flag = ChunkFlag.ALLOW_ICE_MELT
        )
    }
}
