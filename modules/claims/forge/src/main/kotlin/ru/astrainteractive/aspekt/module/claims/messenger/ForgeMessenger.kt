package ru.astrainteractive.aspekt.module.claims.messenger

import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

class ForgeMessenger(
    private val kyoriKrate: Krate<KyoriComponentSerializer>,
) : Messenger {
    override fun sendMessage(
        player: ClaimPlayer,
        stringDesc: StringDesc
    ) {
        val player = ForgeUtil.getOnlinePlayer(player.uuid) ?: return
        kyoriKrate.withAudience(player).sendSystemMessage(stringDesc)
    }
}
