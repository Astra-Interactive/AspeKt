@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.adminprivate.util

import org.bukkit.Chunk
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk

internal inline val AdminChunk.uniqueWorldKey: String
    get() = "${this.chunkKey}_${this.worldName}"

internal inline val Chunk.adminChunk: AdminChunk
    get() = AdminChunk(
        x = x,
        z = z,
        worldName = world.name,
        flags = emptyMap(),
        chunkKey = chunkKey
    )
