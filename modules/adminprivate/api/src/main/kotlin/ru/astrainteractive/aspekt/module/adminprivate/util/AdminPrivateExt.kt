@file:Suppress("Filename")

package ru.astrainteractive.aspekt.module.adminprivate.util

import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk

internal inline val AdminChunk.uniqueWorldKey: String
    get() = "${this.chunkKey}_${this.worldName}"
