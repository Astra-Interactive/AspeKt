package ru.astrainteractive.aspekt.module.claims.server.location

import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.server.location.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.util.getClaimChunk
import ru.astrainteractive.astralibs.server.player.MinecraftOnlineKPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.util.tryCast

class ForgeChunkProvider : ChunkProvider {
    override fun getChunk(player: OnlineKPlayer): ClaimChunk {
        @Suppress("MaxLineLength")
        return player.tryCast<MinecraftOnlineKPlayer>()
            ?.instance
            ?.getClaimChunk()
            ?: error(
                "Could not convert OnlineKPlayer into MinecraftOnlineKPlayer. " +
                    "This should not happen. Contact developer."
            )
    }
}
