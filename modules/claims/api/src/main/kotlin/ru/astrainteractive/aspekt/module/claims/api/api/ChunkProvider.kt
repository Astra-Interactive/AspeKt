package ru.astrainteractive.aspekt.module.claims.api.api

import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

interface ChunkProvider {
    fun findChunk(player: OnlineKPlayer): ClaimChunk
}
