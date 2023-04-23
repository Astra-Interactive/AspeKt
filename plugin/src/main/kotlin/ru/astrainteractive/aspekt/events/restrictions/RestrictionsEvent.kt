@file:OptIn(UnsafeApi::class)
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
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.di.Dependency
import ru.astrainteractive.astralibs.di.Module
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.events.DSLEvent
import ru.astrainteractive.astralibs.events.GlobalEventListener

class RestrictionsEvent(
    pluginConfigurationModule: Dependency<PluginConfiguration>,
    private val bukkitDispatchers: BukkitDispatchers
) {
    private val plugin by AspeKt
    private val pluginConfiguration by pluginConfigurationModule
    private val restrictions: PluginConfiguration.Restrictions
        get() = pluginConfiguration.restrictions

    // Explosions
    val onBlockExplode = DSLEvent<BlockExplodeEvent>(GlobalEventListener, plugin) {
        if (restrictions.explode) it.isCancelled = true
    }
    val onEntityExplode = DSLEvent<BlockExplodeEvent>(GlobalEventListener, plugin) {
        if (!restrictions.explode) it.isCancelled = true
    }
    val onPrimeExplosion = DSLEvent<ExplosionPrimeEvent>(GlobalEventListener, plugin) {
        if (!restrictions.explode) it.isCancelled = true
    }

    // Placing
    val bucketEmptyEvent = DSLEvent<PlayerBucketEmptyEvent>(GlobalEventListener, plugin) {
        when (it.bucket) {
            Material.LAVA_BUCKET -> {
                if (!restrictions.placeLava) it.isCancelled = true
            }

            else -> Unit
        }
    }
    val blockPlace = DSLEvent<BlockPlaceEvent>(GlobalEventListener, plugin) {
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
    val blockFromTo = DSLEvent<BlockFromToEvent>(GlobalEventListener, plugin) {

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
    val blockIgniteEvent = DSLEvent<BlockIgniteEvent>(GlobalEventListener, plugin) {
        if (it.cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return@DSLEvent
        if (!restrictions.spreadFire) it.isCancelled = true
    }
    val blockBurnEvent = DSLEvent<BlockBurnEvent>(GlobalEventListener, plugin) {
        if (!restrictions.spreadFire) it.isCancelled = true
    }
    val blockSpread = DSLEvent<BlockSpreadEvent>(GlobalEventListener, plugin) {

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