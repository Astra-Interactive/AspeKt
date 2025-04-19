package ru.astrainteractive.aspekt.module.sethome.teleport

import kotlinx.coroutines.supervisorScope
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.teleport.TeleportApi

class ForgeTeleportApi : TeleportApi {
    override suspend fun teleport(
        player: OnlineMinecraftPlayer,
        location: Location
    ) = supervisorScope {
        val player = ForgeUtil.getOnlinePlayer(player.uuid) ?: return@supervisorScope
        val level = ForgeUtil.requireServer()
            .allLevels
            .firstOrNull { (it.level.levelData as ServerLevelData).levelName == location.worldName }
            ?: return@supervisorScope
        player.teleportTo(
            level,
            location.x,
            location.y,
            location.z,
            0f,
            0f
        )
    }
}
