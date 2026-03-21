package ru.astrainteractive.aspekt.module.claims.server.location

import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer

interface ChunkProvider {
    fun getChunk(player: OnlineKPlayer): ClaimChunk
}
