package ru.astrainteractive.aspekt.module.adminprivate.model

import kotlinx.serialization.Serializable

@Serializable
internal enum class ChunkFlag {
    BREAK, PLACE, INTERACT, EXPLODE, EMPTY_BUCKET, SPREAD, RECEIVE_DAMAGE, HOSTILE_MOB_SPAWN
}
