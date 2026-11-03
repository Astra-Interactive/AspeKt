@file:Suppress("KindImportDirection")

package ru.astrainteractive.aspekt.module.claims.forge.chunk

import ru.astrainteractive.aspekt.module.claims.api.api.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.forge.util.findClaimChunk
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
