package ru.astrainteractive.aspekt.core.forge.minecraft.teleport

import kotlinx.coroutines.supervisorScope
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.model.getLocation
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.teleport.TeleportApi
import java.util.UUID

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

    override suspend fun teleport(fromPlayer: UUID, toPlayer: UUID) {
        val fromPlayer = ForgeUtil.getOnlinePlayer(fromPlayer) ?: return
        val toPlayer = ForgeUtil.getOnlinePlayer(toPlayer) ?: return
        teleport(
            OnlineMinecraftPlayer(
                uuid = fromPlayer.uuid,
                name = fromPlayer.name.toPlain()
            ),
            toPlayer.getLocation()
        )
    }
}
