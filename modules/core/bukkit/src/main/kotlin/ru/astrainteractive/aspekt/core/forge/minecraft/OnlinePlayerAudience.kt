package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.minecraft.Audience
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

class OnlinePlayerAudience(private val instance: OnlineMinecraftPlayer) : Audience {
    override fun sendMessage(component: Component) {
        Bukkit.getPlayer(instance.uuid)?.sendMessage(component)
    }

    @AutoService(Audience.Factory::class)
    class Factory : Audience.Factory<OnlineMinecraftPlayer> {
        override fun from(instance: OnlineMinecraftPlayer): Audience {
            return OnlinePlayerAudience(instance)
        }
    }
}
