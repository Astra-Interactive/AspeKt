@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.claims.api.util

import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.api.model.UniqueWorldKey

inline val ClaimChunk.uniqueWorldKey: UniqueWorldKey
    get() = UniqueWorldKey("${this.chunkKey}_${this.worldName}")
