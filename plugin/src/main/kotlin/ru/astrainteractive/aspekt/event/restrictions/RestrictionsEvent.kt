@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.event.restrictions

import org.bukkit.Material
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.event.restrictions.di.RestrictionsDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.DSLEvent
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue

class RestrictionsEvent(
    module: RestrictionsDependencies
) : RestrictionsDependencies by module {
    private val restrictions: PluginConfiguration.Restrictions by Provider {
        configuration.restrictions
    }

    // Explosions
    val onBlockExplode = DSLEvent<BlockExplodeEvent>(eventListener, plugin) {
        if (restrictions.explode) it.isCancelled = true
    }
    val onEntityExplode = DSLEvent<BlockExplodeEvent>(eventListener, plugin) {
        if (restrictions.explode) it.isCancelled = true
    }
    val onPrimeExplosion = DSLEvent<ExplosionPrimeEvent>(eventListener, plugin) {
        if (restrictions.explode) it.isCancelled = true
    }

    // Placing
    val bucketEmptyEvent = DSLEvent<PlayerBucketEmptyEvent>(eventListener, plugin) {
        when (it.bucket) {
            Material.LAVA_BUCKET -> {
                if (restrictions.placeLava) it.isCancelled = true
            }

            else -> Unit
        }
    }
    val blockPlace = DSLEvent<BlockPlaceEvent>(eventListener, plugin) {
        when (it.blockPlaced.type) {
            Material.TNT -> {
                if (restrictions.placeTnt) it.isCancelled = true
            }

            Material.LAVA -> {
                if (restrictions.placeLava) it.isCancelled = true
            }

            Material.LAVA_BUCKET -> {
                if (restrictions.placeLava) it.isCancelled = true
            }

            else -> Unit
        }
    }
    val blockFromTo = DSLEvent<BlockFromToEvent>(eventListener, plugin) {

        when (it.block.type) {
            Material.LAVA -> {
                if (restrictions.spreadLava) it.isCancelled = true
            }

            Material.FIRE -> {
                if (restrictions.spreadFire) it.isCancelled = true
            }

            else -> Unit
        }
    }
    val blockIgniteEvent = DSLEvent<BlockIgniteEvent>(eventListener, plugin) {
        if (it.cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return@DSLEvent
        if (restrictions.spreadFire) it.isCancelled = true
    }
    val blockBurnEvent = DSLEvent<BlockBurnEvent>(eventListener, plugin) {
        if (restrictions.spreadFire) it.isCancelled = true
    }
    val blockSpread = DSLEvent<BlockSpreadEvent>(eventListener, plugin) {

        when (it.source.type) {
            Material.LAVA -> {
                if (restrictions.spreadLava) it.isCancelled = true
            }

            Material.FIRE -> {
                if (restrictions.spreadFire) it.isCancelled = true
            }

            else -> Unit
        }
    }
}
