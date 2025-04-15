@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.claims.util

import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.UniqueWorldKey

internal inline val ClaimChunk.uniqueWorldKey: UniqueWorldKey
    get() = UniqueWorldKey("${this.chunkKey}_${this.worldName}")
