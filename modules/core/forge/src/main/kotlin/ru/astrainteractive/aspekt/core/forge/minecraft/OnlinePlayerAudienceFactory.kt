package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.core.forge.util.toNative
import ru.astrainteractive.aspekt.minecraft.Audience
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

@AutoService(Audience.Factory::class)
class OnlinePlayerAudienceFactory : Audience.Factory<OnlineMinecraftPlayer> {
    override fun from(instance: OnlineMinecraftPlayer): Audience {
        return Audience { component ->
            val player = ForgeUtil.getOnlinePlayer(instance.uuid) ?: return@Audience
            player.sendSystemMessage(component.toNative())
        }
    }
}
