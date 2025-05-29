package ru.astrainteractive.aspekt.minecraft

import ru.astrainteractive.aspekt.minecraft.player.OfflineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import java.util.UUID

interface PlatformServer {
    fun getOnlinePlayers(): List<OnlineMinecraftPlayer>

    fun findOnlinePlayer(uuid: UUID): OnlineMinecraftPlayer?

    fun findOfflinePlayer(uuid: UUID): OfflineMinecraftPlayer?

    fun findOnlinePlayer(name: String): OnlineMinecraftPlayer?

    fun findOfflinePlayer(name: String): OfflineMinecraftPlayer?
}
