package ru.astrainteractive.aspekt.module.newbee.event

import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.astrainteractive.aspekt.module.newbee.event.di.EventDependencies
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeConstants
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeExt.isNewBee
import ru.astrainteractive.astralibs.event.EventListener

internal class NewBeeEventListener(
    dependencies: EventDependencies
) : EventListener, EventDependencies by dependencies {

    private fun createInfinitePotionEffect(type: PotionEffectType, amplifier: Int): PotionEffect {
        return PotionEffect(
            type,
            PotionEffect.INFINITE_DURATION,
            amplifier,
            false,
            false,
            false
        )
    }

    private fun getNewBeeEffects(): List<PotionEffect> {
        return buildList {
            createInfinitePotionEffect(
                PotionEffectType.HEAL,
                5,
            ).run(::add)
            createInfinitePotionEffect(
                PotionEffectType.ABSORPTION,
                2,
            ).run(::add)
            createInfinitePotionEffect(
                PotionEffectType.FAST_DIGGING,
                3,
            ).run(::add)
            createInfinitePotionEffect(
                PotionEffectType.REGENERATION,
                4,
            ).run(::add)
            createInfinitePotionEffect(
                PotionEffectType.FIRE_RESISTANCE,
                4,
            ).run(::add)
        }
    }

    private fun Player.giveNewBeeEffects() = scope.launch(dispatcher.Main) {
        withContext(dispatcher.IO) { delay(5.seconds) }
        val effects = getNewBeeEffects()
        addPotionEffects(effects)
        val message = kyoriComponentSerializer.toComponent(translation.newBee.youAreNewBee)
        sendMessage(message)
        showTitle(
            Title.title(
                kyoriComponentSerializer.toComponent(translation.newBee.newBeeTitle),
                kyoriComponentSerializer.toComponent(translation.newBee.newBeeSubtitle),
                Title.Times.times(
                    1.seconds.toJavaDuration(),
                    3.seconds.toJavaDuration(),
                    1.seconds.toJavaDuration()
                )
            )
        )
    }

    private fun Player.clearNewBeeEffects() {
        getNewBeeEffects().map(PotionEffect::getType).forEach(::removePotionEffect)
    }

    @EventHandler
    fun onNewBeeAttacked(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        if (!player.isNewBee) return
        e.damage *= NewBeeConstants.NEW_BEE_DAMAGED_PERCENT
    }

    @EventHandler
    fun onNewBeeSpawn(e: PlayerRespawnEvent) {
        val player = e.player
        if (!player.isNewBee) return
        player.giveNewBeeEffects()
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        if (!player.isNewBee) {
            player.clearNewBeeEffects()
        } else {
            player.giveNewBeeEffects()
        }
    }
}
