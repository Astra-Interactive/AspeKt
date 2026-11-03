@file:Suppress("KindImportDirection")

package ru.astrainteractive.aspekt.module.claims.bukkit.chunk

import ru.astrainteractive.aspekt.module.claims.api.api.ChunkProvider
import ru.astrainteractive.aspekt.module.claims.api.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.bukkit.util.asClaimChunk
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.astralibs.server.util.asBukkitLocation

internal class BukkitChunkProvider : ChunkProvider {
    override fun findChunk(player: OnlineKPlayer): ClaimChunk {
        return player.getLocation().asBukkitLocation().chunk.asClaimChunk()
    }
}
