package ru.astrainteractive.aspekt.core.forge.minecraft

import ru.astrainteractive.aspekt.core.forge.permission.ForgeLuckPermsPlayerPermissible
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.asOnlineMinecraftPlayer
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.Audience
import ru.astrainteractive.aspekt.minecraft.Locatable
import ru.astrainteractive.aspekt.minecraft.MinecraftNativeBridge
import ru.astrainteractive.aspekt.minecraft.Teleportable
import ru.astrainteractive.aspekt.minecraft.player.MinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.player.OfflineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.astralibs.permission.Permissible
import java.util.UUID

class ForgeMinecraftNativeBridge : MinecraftNativeBridge {
    override fun OnlineMinecraftPlayer.asAudience(): Audience {
        return OnlinePlayerAudience(this)
    }

    override fun OnlineMinecraftPlayer.asLocatable(): Locatable {
        return OnlinePlayerLocatable(this)
    }

    override fun OnlineMinecraftPlayer.asTeleportable(): Teleportable {
        return OnlinePlayerTeleportable(this)
    }

    override fun MinecraftPlayer.asPermissible(): Permissible {
        return ForgeLuckPermsPlayerPermissible(uuid)
    }

    private fun findOnlinePlayer(uuid: UUID): OnlineMinecraftPlayer? {
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return null
        return player.asOnlineMinecraftPlayer()
    }

    private fun findOfflinePlayer(uuid: UUID): OfflineMinecraftPlayer? {
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return null
        return OfflineMinecraftPlayer(
            uuid = player.uuid,
        )
    }

    override fun findPlayer(uuid: UUID): MinecraftPlayer? {
        return findOnlinePlayer(uuid) ?: findOfflinePlayer(uuid)
    }
}
