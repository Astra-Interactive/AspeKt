package ru.astrainteractive.aspekt.module.spawn.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.entity.player.PlayerEvent
import ru.astrainteractive.aspekt.core.forge.event.flowEvent
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.location.Location
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.teleport.TeleportApi
import ru.astrainteractive.aspekt.util.cast
import ru.astrainteractive.aspekt.util.tryCast

class SpawnEvent(
    private val scope: CoroutineScope,
    private val teleportApi: TeleportApi
) {
    val respawnEvent = flowEvent<PlayerEvent.PlayerRespawnEvent>()
        .onEach { event ->
            val serverPlayer = event.entity.tryCast<ServerPlayer>() ?: return@onEach
            val respawnPosition = serverPlayer.respawnPosition
            if (respawnPosition != null) return@onEach
            scope.launch {
                teleportApi.teleport(
                    OnlineMinecraftPlayer(
                        uuid = serverPlayer.uuid,
                        name = serverPlayer.name.toPlain()
                    ),
                    TODO()
                )
            }
        }.launchIn(scope)
}