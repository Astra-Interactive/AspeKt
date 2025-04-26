package ru.astrainteractive.aspekt.core.forge.minecraft

import com.google.auto.service.AutoService
import net.minecraft.world.level.storage.ServerLevelData
import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.core.forge.util.getOnlinePlayer
import ru.astrainteractive.aspekt.minecraft.Locatable
import ru.astrainteractive.aspekt.minecraft.ServiceStatusProvider
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.util.cast

@AutoService(Locatable.Factory::class)
class OnlinePlayerLocatableFactory :
    Locatable.Factory<OnlineMinecraftPlayer>,
    ServiceStatusProvider by ForgeServiceStatusProvider {
    override fun from(instance: OnlineMinecraftPlayer): Locatable {
        return Locatable {
            val player = ForgeUtil.getOnlinePlayer(instance.uuid) ?: error("$instance is not online")
            Location(
                x = player.x,
                y = player.y,
                z = player.z,
                worldName = player.level().levelData.cast<ServerLevelData>().levelName
            )
        }
    }
}
