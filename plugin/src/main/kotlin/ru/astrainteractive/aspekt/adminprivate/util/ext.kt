@file:Suppress("Filename")

package ru.astrainteractive.aspekt.adminprivate.util

import org.bukkit.Chunk
import ru.astrainteractive.aspekt.adminprivate.model.AdminChunk

inline val AdminChunk.uniqueWorldKey: String
    get() = "${this.chunkKey}_${this.worldName}"

inline val Chunk.adminChunk: AdminChunk
    get() = AdminChunk(
        x = x,
        z = z,
        worldName = world.name,
        flags = emptyMap(),
        chunkKey = chunkKey
    )
