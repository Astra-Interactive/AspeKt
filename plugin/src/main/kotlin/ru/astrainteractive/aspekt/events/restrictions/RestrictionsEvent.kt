package ru.astrainteractive.aspekt.events.restrictions

import org.bukkit.Material
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.Module
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent

class RestrictionsEvent(
    pluginConfigurationModule: Dependency<PluginConfiguration>
) {
    private val pluginConfiguration by pluginConfigurationModule
    private val restrictions: PluginConfiguration.Restrictions
        get() = pluginConfiguration.restrictions

    // Explosions
    val onBlockExplode = DSLEvent.event<BlockExplodeEvent> {
        if (restrictions.explode) it.isCancelled = true
    }
    val onEntityExplode = DSLEvent.event<BlockExplodeEvent> {
        if (!restrictions.explode) it.isCancelled = true
    }
    val onPrimeExplosion = DSLEvent.event<ExplosionPrimeEvent> {
        if (!restrictions.explode) it.isCancelled = true
    }

    // Placing
    val bucketEmptyEvent = DSLEvent.event<PlayerBucketEmptyEvent> {
        when (it.bucket) {
            Material.LAVA_BUCKET -> {
                if (!restrictions.placeLava) it.isCancelled = true
            }

            else -> Unit
        }
    }
    val blockPlace = DSLEvent.event<BlockPlaceEvent> {
        when (it.blockPlaced.type) {
            Material.TNT -> {
                if (!restrictions.placeTnt) it.isCancelled = true
            }

            Material.LAVA -> {
                if (!restrictions.placeLava) it.isCancelled = true
            }

            Material.LAVA_BUCKET -> {
                if (!restrictions.placeLava) it.isCancelled = true
            }

            else -> Unit
        }
    }
    val blockFromTo = DSLEvent.event<BlockFromToEvent> {

        when (it.block.type){
            Material.LAVA -> {
                if (!restrictions.spreadLava) it.isCancelled = true
            }

            Material.FIRE -> {
                if (!restrictions.spreadFire) it.isCancelled = true
            }

            else -> Unit
        }
    }
    val blockIgniteEvent = DSLEvent.event<BlockIgniteEvent> {
        if (it.cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return@event
        if (!restrictions.spreadFire) it.isCancelled = true
    }
    val blockBurnEvent = DSLEvent.event<BlockBurnEvent> {
        if (!restrictions.spreadFire) it.isCancelled = true
    }
    val blockSpread = DSLEvent.event<BlockSpreadEvent> {

        when (it.source.type) {
            Material.LAVA -> {
                if (!restrictions.spreadLava) it.isCancelled = true
            }

            Material.FIRE -> {
                if (!restrictions.spreadFire) it.isCancelled = true
            }

            else -> Unit
        }
    }
}