package ru.astrainteractive.aspekt.module.newbee.event

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.astrainteractive.aspekt.module.newbee.event.di.EventDependencies
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeConstants
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeExt.isNewBee
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeExt.newBeeShieldDurationLeft
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeExt.ticks
import ru.astrainteractive.astralibs.event.EventListener
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

internal class NewBeeEventListener(
    dependencies: EventDependencies
) : EventListener, EventDependencies by dependencies {

    private fun createInfinitePotionEffect(player: Player, type: PotionEffectType, amplifier: Int): PotionEffect {
        return PotionEffect(
            type,
            player.newBeeShieldDurationLeft.ticks,
            amplifier,
            false,
            false,
            false
        )
    }

    private fun getNewBeeEffects(player: Player): List<PotionEffect> {
        return buildList {
            createInfinitePotionEffect(
                player,
                PotionEffectType.HEAL,
                5,
            ).run(::add)
            createInfinitePotionEffect(
                player,
                PotionEffectType.ABSORPTION,
                2,
            ).run(::add)
            createInfinitePotionEffect(
                player,
                PotionEffectType.FAST_DIGGING,
                3,
            ).run(::add)
            createInfinitePotionEffect(
                player,
                PotionEffectType.REGENERATION,
                4,
            ).run(::add)
            createInfinitePotionEffect(
                player,
                PotionEffectType.FIRE_RESISTANCE,
                4,
            ).run(::add)
        }
    }

    private fun Player.giveNewBeeEffects() = scope.launch(dispatcher.Main) {
        withContext(dispatcher.IO) { delay(5.seconds) }
        val effects = getNewBeeEffects(this@giveNewBeeEffects)
        addPotionEffects(effects)
        val message = kyoriComponentSerializer.toComponent(translation.newBee.youAreNewBee)
        sendMessage(message)
        Title.title(
            kyoriComponentSerializer.toComponent(translation.newBee.newBeeTitle),
            kyoriComponentSerializer.toComponent(translation.newBee.newBeeSubtitle),
            Title.Times.times(
                1.seconds.toJavaDuration(),
                3.seconds.toJavaDuration(),
                1.seconds.toJavaDuration()
            )
        ).run(::showTitle)
    }

    private fun Player.takeNewBeeEffects() = scope.launch(dispatcher.Main) {
        val effects = getNewBeeEffects(this@takeNewBeeEffects)
        addPotionEffects(effects)
        val message = kyoriComponentSerializer.toComponent(translation.newBee.newBeeShieldForceDisabled)
        sendMessage(message)
    }

    private fun Player.clearNewBeeEffects() {
        getNewBeeEffects(this).map(PotionEffect::getType).forEach(::removePotionEffect)
    }

    @EventHandler
    fun onNewBeeAttacked(e: EntityDamageEvent) {
        val player = e.entity as? Player ?: return
        if (!player.isNewBee) return
        e.damage *= NewBeeConstants.NEW_BEE_DAMAGED_PERCENT
    }

    @EventHandler
    fun onNewBeeAttackPlayer(e: EntityDamageByEntityEvent) {
        if (e.entity !is Player) return
        val newBeeAttacker = e.damager as? Player ?: return
        if (!newBeeAttacker.isNewBee) return
        newBeeAttacker.takeNewBeeEffects()
    }

    @EventHandler
    fun onPlayerAttackNewBee(e: EntityDamageByEntityEvent) {
        if (e.damager !is Player) return
        val newBee = e.entity as? Player ?: return
        if (!newBee.isNewBee) return
        newBee.takeNewBeeEffects()
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
