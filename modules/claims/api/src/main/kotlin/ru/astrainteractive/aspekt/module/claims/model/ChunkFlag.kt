package ru.astrainteractive.aspekt.module.claims.model

import kotlinx.serialization.Serializable

@Serializable
enum class ChunkFlag {
    ALLOW_BREAK,
    ALLOW_PLACE,
    ALLOW_INTERACT,
    ALLOW_EXPLODE,
    ALLOW_EMPTY_BUCKET,
    ALLOW_SPREAD,
    ALLOW_RECEIVE_DAMAGE,
    ALLOW_HOSTILE_MOB_SPAWN,
    ALLOW_ICE_MELT
}
