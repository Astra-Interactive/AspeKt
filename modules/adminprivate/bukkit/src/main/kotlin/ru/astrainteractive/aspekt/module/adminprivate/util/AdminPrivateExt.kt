@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.adminprivate.util

import org.bukkit.Chunk
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimPlayer

internal inline val Chunk.claimChunk: ClaimChunk
    get() = ClaimChunk(
        x = x,
        z = z,
        worldName = world.name,
        flags = emptyMap(),
        chunkKey = chunkKey
    )

internal inline val Player.claimPlayer: ClaimPlayer
    get() = ClaimPlayer(
        uuid = uniqueId,
        username = name
    )