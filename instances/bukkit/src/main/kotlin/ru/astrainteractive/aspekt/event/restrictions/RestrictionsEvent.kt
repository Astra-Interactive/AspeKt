@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.event.restrictions

import org.bukkit.Material
import org.bukkit.entity.EntityType
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
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener

class RestrictionsEvent(
    dependencies: RestrictionsDependencies
) : RestrictionsDependencies by dependencies, EventListener {
    private val restrictions: PluginConfiguration.Restrictions
        get() = configuration.restrictions

    // Explosions
    @EventHandler
    fun onBlockExplode(it: BlockExplodeEvent) {
        if (restrictions.explosion.destroy) it.isCancelled = true
    }

    @EventHandler
    fun onEntityExplode(it: EntityExplodeEvent) {
        if (!restrictions.explosion.destroy) return
        if (it.entityType == EntityType.WIND_CHARGE) return
        if (it.entityType == EntityType.BREEZE_WIND_CHARGE) return
        it.isCancelled = true
    }

    @EventHandler
    fun onPrimeExplosion(it: ExplosionPrimeEvent) {
        it.fire = false
        if (!restrictions.explosion.creeperDamage) return
        if (it.entity.type == EntityType.CREEPER) it.radius = 0f
    }

    // Placing
    @EventHandler
    fun bucketEmptyEvent(it: PlayerBucketEmptyEvent) {
        when (it.bucket) {
            Material.LAVA_BUCKET -> {
                if (restrictions.place.lava) it.isCancelled = true
            }

            else -> Unit
        }
    }

    @EventHandler
    fun blockPlace(it: BlockPlaceEvent) {
        when (it.blockPlaced.type) {
            Material.TNT -> {
                if (restrictions.place.tnt) it.isCancelled = true
            }

            Material.LAVA -> {
                if (restrictions.place.lava) it.isCancelled = true
            }

            Material.LAVA_BUCKET -> {
                if (restrictions.place.lava) it.isCancelled = true
            }

            else -> Unit
        }
    }

    @EventHandler
    fun blockFromTo(it: BlockFromToEvent) {
        when (it.block.type) {
            Material.LAVA -> {
                if (restrictions.spread.lava) it.isCancelled = true
            }

            Material.FIRE -> {
                if (restrictions.spread.fire) it.isCancelled = true
            }

            else -> Unit
        }
    }

    @EventHandler
    fun blockIgniteEvent(it: BlockIgniteEvent) {
        if (it.cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return
        if (restrictions.spread.fire) it.isCancelled = true
    }

    @EventHandler
    fun blockBurnEvent(it: BlockBurnEvent) {
        if (restrictions.spread.fire) it.isCancelled = true
    }

    @EventHandler
    fun blockSpread(it: BlockSpreadEvent) {
        when (it.source.type) {
            Material.LAVA -> {
                if (restrictions.spread.lava) it.isCancelled = true
            }

            Material.FIRE -> {
                if (restrictions.spread.fire) it.isCancelled = true
            }

            else -> Unit
        }
    }
}
