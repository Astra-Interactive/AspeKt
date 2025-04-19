package ru.astrainteractive.aspekt.core.forge.minecraft.messenger

import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.messenger.MinecraftMessenger
import ru.astrainteractive.aspekt.minecraft.player.MinecraftPlayer
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate
import java.util.UUID

class ForgeMinecraftMessenger(
    private val kyoriKrate: Krate<KyoriComponentSerializer>,
) : MinecraftMessenger {
    override fun send(player: MinecraftPlayer, stringDesc: StringDesc) {
        val player = ForgeUtil.getOnlinePlayer(player.uuid) ?: return
        kyoriKrate.withAudience(player).sendSystemMessage(stringDesc)
    }

    override fun send(uuid: UUID, stringDesc: StringDesc) {
        val player = ForgeUtil.getOnlinePlayer(uuid) ?: return
        kyoriKrate.withAudience(player).sendSystemMessage(stringDesc)
    }
}
