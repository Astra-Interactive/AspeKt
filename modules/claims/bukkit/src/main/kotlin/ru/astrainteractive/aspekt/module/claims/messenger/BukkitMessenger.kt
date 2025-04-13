package ru.astrainteractive.aspekt.module.claims.messenger

import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

class BukkitMessenger(
    private val kyoriKrate: Krate<KyoriComponentSerializer>
) : Messenger {
    override fun sendMessage(
        player: ClaimPlayer,
        stringDesc: StringDesc
    ): Unit = with(kyoriKrate.cachedValue) {
        Bukkit.getPlayer(player.uuid)?.sendMessage(stringDesc.component)
    }
}
