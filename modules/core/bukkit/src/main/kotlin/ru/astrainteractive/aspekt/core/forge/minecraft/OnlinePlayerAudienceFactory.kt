package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.minecraft.Audience
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

@AutoService(Audience.Factory::class)
class OnlinePlayerAudienceFactory : Audience.Factory<OnlineMinecraftPlayer> {
    override fun from(instance: OnlineMinecraftPlayer): Audience {
        return Audience { component ->
            Bukkit.getPlayer(instance.uuid)?.sendMessage(component)
        }
    }
}
