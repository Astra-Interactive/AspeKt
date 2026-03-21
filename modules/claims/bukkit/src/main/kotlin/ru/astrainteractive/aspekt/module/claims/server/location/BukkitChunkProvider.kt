package ru.astrainteractive.aspekt.module.claims.server.location

import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.asClaimChunk
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astralibs.server.util.asBukkitLocation

class BukkitChunkProvider : ChunkProvider {
    override fun getChunk(player: OnlineKPlayer): ClaimChunk {
        return player.getLocation().asBukkitLocation().chunk.asClaimChunk()
    }
}
