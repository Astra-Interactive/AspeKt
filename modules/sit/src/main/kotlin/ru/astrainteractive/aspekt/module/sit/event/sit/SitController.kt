package ru.astrainteractive.aspekt.module.sit.event.sit

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate

class SitController(
    configuration: Krate<PluginConfiguration>,
    translation: Krate<PluginTranslation>,
    kyoriComponentSerializer: KyoriComponentSerializer
) : KyoriComponentSerializer by kyoriComponentSerializer {
    private val translation by translation
    private val configuration by configuration

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
        if (!configuration.sit) return
        if (isFilledWithSolidBlocks(location)) {
            player.sendMessage(translation.sit.cantSitInBlock.let(::toComponent))
            return
        }
        if (player.location.distance(location) > MAX_DISTANCE) {
            player.sendMessage(translation.sit.tooFar.let(::toComponent))
            return
        }
        // Сидит ли уже игрок
        if (sitPlayers.contains(player.uniqueId.toString())) {
            player.sendMessage(translation.sit.sitAlready.let(::toComponent))
            return
        }
        // Находится ли игрок в воздухе
        if (player.isFlying) {
            player.sendMessage(translation.sit.sitInAir.let(::toComponent))
            return
        }
        // Находится ли игрок в воздухе
        if (player.location.block.getRelative(BlockFace.DOWN).type == Material.AIR) {
            player.sendMessage(translation.sit.sitInAir.let(::toComponent))
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
        for (player in sitPlayers.keys)
            sitPlayers[player]!!.remove()
        sitPlayers.clear()
    }

    companion object {
        private const val MAX_DISTANCE = 2
        private const val SIT_STAIR_OFFSET = 1.6
    }
}
