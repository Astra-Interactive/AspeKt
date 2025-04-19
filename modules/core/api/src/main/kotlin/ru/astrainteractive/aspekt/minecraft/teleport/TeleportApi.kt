package ru.astrainteractive.aspekt.minecraft.teleport

import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import java.util.UUID

interface TeleportApi {
    suspend fun teleport(player: OnlineMinecraftPlayer, location: Location)

    suspend fun teleport(fromPlayer: UUID, toPlayer: UUID)
}
