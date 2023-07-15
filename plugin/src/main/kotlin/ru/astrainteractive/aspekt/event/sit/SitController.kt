package ru.astrainteractive.aspekt.event.sit

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.event.di.EventsModule

class SitController(
    module: EventsModule
) : EventsModule by module {
    private val sitPlayers = mutableMapOf<String, ArmorStand>()

    /**
     * Заставляет игрока сесть
     */
    fun toggleSitPlayer(player: Player, location: Location = player.location) {
        if (!configuration.sit) return
        // Сидит ли уже игрок
        if (sitPlayers.contains(player.uniqueId.toString())) {
            player.sendMessage(translation.sitAlready)
            return
        }
        // Находится ли игрок в воздухе
        if (player.isFlying) {
            player.sendMessage(translation.sitInAir)
            return
        }
        // Находится ли игрок в воздухе
        if (player.location.block.getRelative(BlockFace.DOWN).type == Material.AIR) {
            player.sendMessage(translation.sitInAir)
            return
        }
        // Создаем стул
        val chair = location.world?.spawnEntity(location.add(0.0, -1.6, 0.0), EntityType.ARMOR_STAND) as ArmorStand
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
        player.teleport(player.location.add(0.0, 1.6, 0.0))
    }

    fun onDisable() {
        for (player in sitPlayers.keys)
            sitPlayers[player]!!.remove()
        sitPlayers.clear()
    }
}