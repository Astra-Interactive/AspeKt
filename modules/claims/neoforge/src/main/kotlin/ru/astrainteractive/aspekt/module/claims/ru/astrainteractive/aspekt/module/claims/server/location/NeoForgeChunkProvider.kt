package ru.astrainteractive.aspekt.module.claims.ru.astrainteractive.aspekt.module.claims.server.location

import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.server.location.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.util.getClaimChunk
import ru.astrainteractive.astralibs.server.player.NeoForgeOnlineKPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.util.cast

class NeoForgeChunkProvider : ChunkProvider {
    override fun getChunk(player: OnlineKPlayer): ClaimChunk {
        return player.cast<NeoForgeOnlineKPlayer>().instance.getClaimChunk()
    }
}
