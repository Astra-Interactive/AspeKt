package ru.astrainteractive.aspekt.adminprivate.models

import kotlinx.serialization.Serializable

@Serializable
enum class ChunkFlag {
    BREAK, PLACE, INTERACT, EXPLODE, EMPTY_BUCKET, SPREAD, RECEIVE_DAMAGE, HOSTILE_MOB_SPAWN
}
