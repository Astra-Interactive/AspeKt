package ru.astrainteractive.aspekt.minecraft.player

import kotlinx.serialization.Serializable
import ru.astrainteractive.aspekt.serialization.UuidSerializer
import java.util.UUID

interface MinecraftPlayer {
    val uuid: UUID
}

@Serializable
class OnlineMinecraftPlayer(
    @Serializable(UuidSerializer::class)
    override val uuid: UUID,
    val name: String
) : MinecraftPlayer

@Serializable
class OfflineMinecraftPlayer(
    @Serializable(UuidSerializer::class)
    override val uuid: UUID
) : MinecraftPlayer
