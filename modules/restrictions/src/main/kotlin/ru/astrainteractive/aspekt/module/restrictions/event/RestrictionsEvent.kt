package ru.astrainteractive.aspekt.module.restrictions.event

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import ru.astrainteractive.aspekt.module.restrictions.model.RestrictionsConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class RestrictionsEvent(
    configKrate: CachedKrate<RestrictionsConfiguration>
) : EventListener {
    private val restrictionsConfiguration by configKrate
    fun RestrictionsConfiguration.RestrictionRule.isRestrictedIn(worldName: String): Boolean {
        if (!isEnabled) return false
        val isMatchesWorld = restrictedInWorlds.isEmpty() || worldName in restrictedInWorlds
        return isMatchesWorld != invert
    }

    private fun cancelIfRestricted(
        event: Cancellable,
        rule: RestrictionsConfiguration.RestrictionRule,
        worldName: String
    ) {
        if (rule.isRestrictedIn(worldName)) event.isCancelled = true
    }

    // Explosions
    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        cancelIfRestricted(
            event = event,
            rule = restrictionsConfiguration.explosion.destroy,
            worldName = event.block.world.name
        )
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val isRestricted = restrictionsConfiguration.explosion
            .destroy
            .isRestrictedIn(event.entity.world.name)
        if (!isRestricted) return
        if (event.entityType == EntityType.WIND_CHARGE) return
        if (event.entityType == EntityType.BREEZE_WIND_CHARGE) return
        event.isCancelled = true
    }

    @EventHandler
    fun onPrimeExplosion(event: ExplosionPrimeEvent) {
        val isFireRestricted = restrictionsConfiguration.spread
            .fire
            .isRestrictedIn(event.entity.world.name)
        if (isFireRestricted) {
            event.fire = false
        }
        val isRestricted = restrictionsConfiguration.explosion
            .creeperDamage
            .isRestrictedIn(event.entity.world.name)
        if (!isRestricted) return
        if (event.entity.type == EntityType.CREEPER) event.radius = 0f
    }

    // Placing
    @EventHandler
    fun bucketEmptyEvent(event: PlayerBucketEmptyEvent) {
        when (event.bucket) {
            Material.LAVA_BUCKET -> cancelIfRestricted(
                event = event,
                rule = restrictionsConfiguration.place.lava,
                worldName = event.block.world.name
            )

            else -> Unit
        }
    }

    @EventHandler
    fun blockPlace(event: BlockPlaceEvent) {
        val worldName = event.blockPlaced.world.name
        when (event.blockPlaced.type) {
            Material.TNT -> cancelIfRestricted(
                event = event,
                rule = restrictionsConfiguration.place.tnt,
                worldName = worldName
            )

            Material.LAVA, Material.LAVA_BUCKET -> cancelIfRestricted(
                event = event,
                rule = restrictionsConfiguration.place.lava,
                worldName = worldName
            )

            else -> Unit
        }
    }

    @EventHandler
    fun blockFromTo(event: BlockFromToEvent) {
        val worldName = event.block.world.name
        when (event.block.type) {
            Material.LAVA -> cancelIfRestricted(
                event = event,
                rule = restrictionsConfiguration.spread.lava,
                worldName = worldName
            )

            Material.FIRE -> cancelIfRestricted(
                event = event,
                rule = restrictionsConfiguration.spread.fire,
                worldName = worldName
            )

            else -> Unit
        }
    }

    @EventHandler
    fun blockIgniteEvent(event: BlockIgniteEvent) {
        if (event.cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return
        cancelIfRestricted(
            event = event,
            rule = restrictionsConfiguration.spread.fire,
            worldName = event.block.world.name
        )
    }

    @EventHandler
    fun blockBurnEvent(event: BlockBurnEvent) {
        cancelIfRestricted(
            event = event,
            rule = restrictionsConfiguration.spread.fire,
            worldName = event.block.world.name
        )
    }

    @EventHandler
    fun blockSpread(event: BlockSpreadEvent) {
        val worldName = event.block.world.name
        when (event.source.type) {
            Material.LAVA -> cancelIfRestricted(
                event = event,
                rule = restrictionsConfiguration.spread.lava,
                worldName = worldName
            )

            Material.FIRE -> cancelIfRestricted(
                event = event,
                rule = restrictionsConfiguration.spread.fire,
                worldName = worldName
            )

            else -> Unit
        }
    }
}
