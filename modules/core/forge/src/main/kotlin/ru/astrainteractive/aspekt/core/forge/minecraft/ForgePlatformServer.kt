package ru.astrainteractive.aspekt.core.forge.minecraft

import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.asOfflineMinecraftPlayer
import ru.astrainteractive.aspekt.core.forge.util.asOnlineMinecraftPlayer
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayers
import ru.astrainteractive.aspekt.core.forge.util.getPlayerGameProfile
import ru.astrainteractive.aspekt.minecraft.PlatformServer
import ru.astrainteractive.aspekt.minecraft.player.OfflineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import java.util.UUID

object ForgePlatformServer : PlatformServer {
    override fun getOnlinePlayers(): List<OnlineMinecraftPlayer> {
        return ForgeUtil
            .getOnlinePlayers()
            .map { serverPlayer -> serverPlayer.asOnlineMinecraftPlayer() }
    }

    override fun findOnlinePlayer(uuid: UUID): OnlineMinecraftPlayer? {
        return ForgeUtil.getOnlinePlayer(uuid)?.asOnlineMinecraftPlayer()
    }

    override fun findOfflinePlayer(uuid: UUID): OfflineMinecraftPlayer? {
        return ForgeUtil.getPlayerGameProfile(uuid)?.asOfflineMinecraftPlayer()
    }

    override fun findOnlinePlayer(name: String): OnlineMinecraftPlayer? {
        return ForgeUtil.getOnlinePlayer(name)?.asOnlineMinecraftPlayer()
    }

    override fun findOfflinePlayer(name: String): OfflineMinecraftPlayer? {
        return ForgeUtil.getPlayerGameProfile(name)?.asOfflineMinecraftPlayer()
    }
}
