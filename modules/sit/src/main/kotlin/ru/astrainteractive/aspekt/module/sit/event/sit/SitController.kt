package ru.astrainteractive.aspekt.module.sit.event.sit

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.sit.model.SitConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class SitController(
    sitKrate: CachedKrate<SitConfiguration>,
    translation: CachedKrate<PluginTranslation>
) {
    private val translation by translation
    private val sitConfig by sitKrate

    private val sitPlayers = mutableMapOf<String, ArmorStand>()

    private fun isFilledWithSolidBlocks(location: Location): Boolean {
        val sitBlock = location.block
        val aboveSitBlock = sitBlock.getRelative(BlockFace.UP)
        val aboveAboveSitBlock = aboveSitBlock.getRelative(BlockFace.UP)
        return aboveSitBlock.isSolid && aboveAboveSitBlock.isSolid
    }

    /**
     * Заставляет игрока сесть
     */
    fun toggleSitPlayer(
        player: Player,
        location: Location = player.location.clone(),
        locationWithOffset: Location = location.clone().add(0.0, -SIT_STAIR_OFFSET, 0.0)
    ) {
        if (!sitConfig.isEnabled) return
        if (isFilledWithSolidBlocks(location)) {
            player.asKAudience().sendMessage(translation.sit.cantSitInBlock)
            return
        }
        if (player.location.distance(location) > MAX_DISTANCE) {
            player.asKAudience().sendMessage(translation.sit.tooFar)
            return
        }
        // Сидит ли уже игрок
        if (sitPlayers.contains(player.uniqueId.toString())) {
            player.asKAudience().sendMessage(translation.sit.sitAlready)
            return
        }
        // Находится ли игрок в воздухе
        if (player.isFlying) {
            player.asKAudience().sendMessage(translation.sit.sitInAir)
            return
        }
        // Находится ли игрок в воздухе
        if (player.location.block.getRelative(BlockFace.DOWN).type == Material.AIR) {
            player.asKAudience().sendMessage(translation.sit.sitInAir)
            return
        }
        // Создаем стул
        val chair = location.world?.spawnEntity(locationWithOffset, EntityType.ARMOR_STAND) as ArmorStand
        chair.setGravity(false)
        chair.isVisible = false
        chair.isInvulnerable = false
        // Садим игрока
        chair.addPassenger(player)
        // Добавялем игрока в список посаженных
        sitPlayers[player.uniqueId.toString()] = chair
    }

    /**
     * Функция заставляет игрока встать
     */
    fun stopSitPlayer(player: Player) {
        // Берем текущий стул игрока
        val armorStand = sitPlayers[player.uniqueId.toString()] ?: return
        // Удаляем стул и убираем игрока из списка
        armorStand.remove()
        sitPlayers.remove(player.uniqueId.toString())
        // Телепортируем чуть повыше
        player.teleport(player.location.add(0.0, SIT_STAIR_OFFSET, 0.0))
    }

    fun onDisable() {
        for (player in sitPlayers.keys) {
            sitPlayers[player]!!.remove()
        }
        sitPlayers.clear()
    }

    companion object {
        private const val MAX_DISTANCE = 2
        private const val SIT_STAIR_OFFSET = 1.6
    }
}
