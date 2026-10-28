@file:Suppress("KindImportDirection")

package ru.astrainteractive.aspekt.module.claims.forge

import ru.astrainteractive.aspekt.module.claims.api.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.findClaimChunk
import ru.astrainteractive.astralibs.server.player.MinecraftOnlineKPlayer
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.util.tryCast

internal class ForgeChunkProvider : ChunkProvider {
    override fun findChunk(player: OnlineKPlayer): ClaimChunk {
        @Suppress("MaxLineLength")
        return player.tryCast<MinecraftOnlineKPlayer>()
            ?.instance
            ?.findClaimChunk()
            ?: error(
                "Could not convert OnlineKPlayer into MinecraftOnlineKPlayer. " +
                    "This should not happen. Contact developer."
            )
    }
}
