package ru.astrainteractive.aspekt.module.auth.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.neoforge.event.entity.EntityEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.event.flowEvent
import ru.astrainteractive.astralibs.event.playerMoveFlowEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.location.dist
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.astralibs.server.util.toPlain
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.util.tryCast

class ForgeAuthEvent(
    private val authorizedApi: AuthorizedApi,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    private val mainScope: CoroutineScope,
    translationKrate: CachedKrate<AuthTranslation>
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    val translation by translationKrate

    val playerLoggedOutEvent = flowEvent<PlayerEvent.PlayerLoggedOutEvent>()
        .onEach { authorizedApi.forgetUser(it.entity.uuid) }
        .launchIn(mainScope)

    val playerLoggedInEvent = flowEvent<PlayerEvent.PlayerLoggedInEvent>()
        .onEach {
            val playerLoginModel = PlayerLoginModel(
                username = it.entity.name.toPlain(),
                uuid = it.entity.uuid,
                ip = (it.entity as ServerPlayer).ipAddress
            )
            authorizedApi.loadUserInfo(playerLoginModel)
        }
        .launchIn(mainScope)

    private fun processPlayerEvent(player: Player) = mainScope.launch {
        when (authorizedApi.getAuthState(player.uuid)) {
            AuthorizedApi.AuthState.Authorized -> Unit

            AuthorizedApi.AuthState.Pending,
            AuthorizedApi.AuthState.NotAuthorized -> {
                player
                    .asKAudience()
                    .sendMessage(translation.notAuthorized.component)
            }

            AuthorizedApi.AuthState.NotRegistered -> {
                player
                    .asKAudience()
                    .sendMessage(translation.notRegistered.component)
            }
        }
    }

    @Suppress("MagicNumber")
    val playerMoveEvent = playerMoveFlowEvent()
        .filter { event -> authorizedApi.getAuthState(event.player.uuid) !is AuthorizedApi.AuthState.Authorized }
        .onEach { event ->
            if (event.newKLocation.dist(event.oldKLocation) > 0.001) {
                processPlayerEvent(event.player)
                event.player.teleportTo(
                    event.oldKLocation.x,
                    event.oldKLocation.y,
                    event.oldKLocation.z
                )
            }
        }.launchIn(mainScope)

    val breakEvent = flowEvent<BlockEvent.BreakEvent>(EventPriority.HIGHEST)
        .filter { it.player.isAlive }
        .filter { authorizedApi.getAuthState(it.player.uuid) !is AuthorizedApi.AuthState.Authorized }
        .onEach { event ->
            println(event)
            event.isCanceled = true
            processPlayerEvent(event.player)
        }.launchIn(mainScope)

    val playerEvent = flowEvent<EntityEvent>(EventPriority.HIGHEST)
        .filter { it.entity is Player }
        .filter { it !is EntityTickEvent }
        .filter { it !is EntityJoinLevelEvent }
        .filter { it !is PlayerEvent.PlayerLoggedInEvent }
        .filter { it.entity.isAlive }
        .filter { authorizedApi.getAuthState(it.entity.uuid) !is AuthorizedApi.AuthState.Authorized }
        .onEach { event ->
            event.tryCast<ICancellableEvent>()?.isCanceled = true
            processPlayerEvent(event.entity as Player)
        }
        .launchIn(mainScope)
}
