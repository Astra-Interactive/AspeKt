package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.Locatable
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.util.cast

class OnlinePlayerLocatable(private val instance: OnlineMinecraftPlayer) : Locatable {
    override fun getLocation(): Location {
        val player = ForgeUtil.getOnlinePlayer(instance.uuid) ?: error("$instance is not online")
        return Location(
            x = player.x,
            y = player.y,
            z = player.z,
            worldName = player.level().levelData.cast<ServerLevelData>().levelName
        )
    }

    @AutoService(Locatable.Factory::class)
    class Factory : Locatable.Factory<OnlineMinecraftPlayer> {
        override fun from(instance: OnlineMinecraftPlayer): Locatable {
            return OnlinePlayerLocatable(instance)
        }
    }
}
