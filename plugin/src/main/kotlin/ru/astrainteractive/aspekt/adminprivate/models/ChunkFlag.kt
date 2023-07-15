package ru.astrainteractive.aspekt.adminprivate.models

import kotlinx.serialization.Serializable

@Serializable
enum class ChunkFlag {
    BREAK, PLACE, INTERACT
}
