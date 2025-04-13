package ru.astrainteractive.aspekt.module.claims.util

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer

internal fun Player.toClaimPlayer(): ClaimPlayer {
    return ClaimPlayer(
        uuid = uuid,
        username = name.toPlain()
    )
}

internal fun ServerPlayer.getClaimChunk(): ClaimChunk {
    val chunkPos = chunkPosition()
    val serverLevel = serverLevel()
    val levelData = serverLevel.levelData as ServerLevelData
    val chunkKey = chunkPos.x.toLong() and 0xffffffffL or ((chunkPos.z.toLong() and 0xffffffffL) shl 32)
    return ClaimChunk(
        x = chunkPos.x,
        z = chunkPos.z,
        worldName = levelData.levelName,
        flags = emptyMap(),
        chunkKey = chunkKey
    )
}
