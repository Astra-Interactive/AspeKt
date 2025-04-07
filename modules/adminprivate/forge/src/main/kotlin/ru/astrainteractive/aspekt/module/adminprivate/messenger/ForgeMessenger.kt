package ru.astrainteractive.aspekt.module.adminprivate.messenger

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.minecraft.server.MinecraftServer
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.module.adminprivate.model.ClaimPlayer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

class ForgeMessenger(
    private val kyoriKrate: Krate<KyoriComponentSerializer>,
    private val serverFlow: Flow<MinecraftServer>
) : Messenger {
    override fun sendMessage(
        player: ClaimPlayer,
        stringDesc: StringDesc
    ) {
        val player = runBlocking {
            serverFlow.first().playerList.getPlayer(player.uuid)
        } ?: return
        kyoriKrate.withAudience(player).sendSystemMessage(stringDesc)
    }
}
