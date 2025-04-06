@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.adminprivate.util

import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.UniqueWorldKey

internal inline val ClaimChunk.uniqueWorldKey: UniqueWorldKey
    get() = UniqueWorldKey("${this.chunkKey}_${this.worldName}")
