package ru.astrainteractive.aspekt.module.newbee.event

import kotlinx.coroutines.CoroutineScope
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
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeConstants
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeExt.isNewBee
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeExt.newBeeShieldDurationLeft
import ru.astrainteractive.aspekt.module.newbee.util.NewBeeExt.ticks
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.event.EventListener
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

internal class NewBeeEventListener(
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<PluginTranslation>,
    private val ioScope: CoroutineScope,
    private val dispatcher: KotlinDispatchers
) : EventListener {
    private val kyori by kyoriKrate
    private val translation by translationKrate

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

    @Suppress("MagicNumber")
    private fun getNewBeeEffects(player: Player): List<PotionEffect> {
        return buildList {
            createInfinitePotionEffect(
                player,
                PotionEffectType.REGENERATION,
                5,
            ).run(::add)
            createInfinitePotionEffect(
                player,
                PotionEffectType.ABSORPTION,
                2,
            ).run(::add)
            createInfinitePotionEffect(
                player,
                PotionEffectType.HASTE,
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

    private fun Player.giveNewBeeEffects() = ioScope.launch(dispatcher.Main) {
        withContext(dispatcher.IO) { delay(5.seconds) }
        val effects = getNewBeeEffects(this@giveNewBeeEffects)
        addPotionEffects(effects)
        val message = kyori.toComponent(translation.newBee.youAreNewBee)
        sendMessage(message)
        Title.title(
            kyori.toComponent(translation.newBee.newBeeTitle),
            kyori.toComponent(translation.newBee.newBeeSubtitle),
            Title.Times.times(
                1.seconds.toJavaDuration(),
                3.seconds.toJavaDuration(),
                1.seconds.toJavaDuration()
            )
        ).run(::showTitle)
    }

    private fun Player.takeNewBeeEffects() = ioScope.launch(dispatcher.Main) {
        if (this@takeNewBeeEffects.activePotionEffects.isEmpty()) return@launch
        getNewBeeEffects(this@takeNewBeeEffects).map(PotionEffect::getType).forEach(::removePotionEffect)
        val message = kyori.toComponent(translation.newBee.newBeeShieldForceDisabled)
        sendMessage(message)
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
        val attacker = e.damager as? Player ?: return
        if (!attacker.isNewBee) return
        attacker.takeNewBeeEffects()
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
            player.takeNewBeeEffects()
        } else {
            player.giveNewBeeEffects()
        }
    }
}
