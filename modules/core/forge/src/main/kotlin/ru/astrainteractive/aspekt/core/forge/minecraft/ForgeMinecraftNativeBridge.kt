package ru.astrainteractive.aspekt.core.forge.minecraft

import ru.astrainteractive.aspekt.minecraft.Audience
import ru.astrainteractive.aspekt.minecraft.Locatable
import ru.astrainteractive.aspekt.minecraft.MinecraftNativeBridge
import ru.astrainteractive.aspekt.minecraft.Teleportable
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

class ForgeMinecraftNativeBridge : MinecraftNativeBridge {
    override fun OnlineMinecraftPlayer.asAudience(): Audience {
        return OnlinePlayerAudience(this)
    }

    override fun OnlineMinecraftPlayer.asLocatable(): Locatable {
        return OnlinePlayerLocatable(this)
    }

    override fun OnlineMinecraftPlayer.asTeleportable(): Teleportable {
        return OnlinePlayerTeleportable(this)
    }
}
