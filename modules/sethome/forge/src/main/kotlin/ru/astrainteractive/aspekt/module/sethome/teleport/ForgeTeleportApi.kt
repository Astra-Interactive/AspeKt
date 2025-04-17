package ru.astrainteractive.aspekt.module.sethome.teleport

import kotlinx.coroutines.supervisorScope
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.module.sethome.model.HomeLocation

class ForgeTeleportApi : TeleportApi {
    override suspend fun teleport(
        player: OnlineMinecraftPlayer,
        homeLocation: HomeLocation
    ) = supervisorScope {
        val player = ForgeUtil.getOnlinePlayer(player.uuid) ?: return@supervisorScope
        val level = ForgeUtil.requireServer()
            .allLevels
            .firstOrNull { (it.level.levelData as ServerLevelData).levelName == homeLocation.worldName }
            ?: return@supervisorScope
        player.teleportTo(
            level,
            homeLocation.x,
            homeLocation.y,
            homeLocation.z,
            0f,
            0f
        )
    }
}
