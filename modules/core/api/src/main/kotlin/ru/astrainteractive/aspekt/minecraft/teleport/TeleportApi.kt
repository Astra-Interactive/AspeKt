package ru.astrainteractive.aspekt.minecraft.teleport

import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

interface TeleportApi {
    suspend fun teleport(player: OnlineMinecraftPlayer, location: Location)
}
