package ru.astrainteractive.aspekt.module.sethome.teleport

import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.module.sethome.model.HomeLocation

interface TeleportApi {
    suspend fun teleport(player: OnlineMinecraftPlayer, homeLocation: HomeLocation)
}
