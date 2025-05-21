package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import net.kyori.adventure.text.Component
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.core.forge.util.toNative
import ru.astrainteractive.aspekt.minecraft.Audience
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

class OnlinePlayerAudience(private val instance: OnlineMinecraftPlayer) : Audience {
    override fun sendMessage(component: Component) {
        val player = ForgeUtil.getOnlinePlayer(instance.uuid) ?: return
        player.sendSystemMessage(component.toNative())
    }

    @AutoService(Audience.Factory::class)
    class Factory : Audience.Factory<OnlineMinecraftPlayer> {
        override fun from(instance: OnlineMinecraftPlayer): Audience {
            return OnlinePlayerAudience(instance)
        }
    }
}
