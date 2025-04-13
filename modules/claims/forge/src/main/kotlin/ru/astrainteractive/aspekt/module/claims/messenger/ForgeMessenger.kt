package ru.astrainteractive.aspekt.module.claims.messenger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import net.minecraft.server.MinecraftServer
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

class ForgeMessenger(
    private val kyoriKrate: Krate<KyoriComponentSerializer>,
    serverFlow: Flow<MinecraftServer>,
    scope: CoroutineScope
) : Messenger {
    private val serverStateFlow = serverFlow.stateIn(scope, SharingStarted.Eagerly, null)
    override fun sendMessage(
        player: ClaimPlayer,
        stringDesc: StringDesc
    ) {
        val server = serverStateFlow.value ?: return
        val player = server.playerList.getPlayer(player.uuid) ?: return
        kyoriKrate.withAudience(player).sendSystemMessage(stringDesc)
    }
}
