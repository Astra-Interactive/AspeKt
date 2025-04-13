@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.claims.util

import org.bukkit.Chunk
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer

internal fun Chunk.asClaimChunk(): ClaimChunk = ClaimChunk(
    x = x,
    z = z,
    worldName = world.name,
    flags = emptyMap(),
    chunkKey = chunkKey
)

internal fun Player.asClaimPlayer(): ClaimPlayer = ClaimPlayer(
    uuid = uniqueId,
    username = name
)
