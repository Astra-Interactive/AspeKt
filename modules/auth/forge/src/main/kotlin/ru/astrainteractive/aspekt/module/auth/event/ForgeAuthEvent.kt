package ru.astrainteractive.aspekt.module.auth.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.EventPriority
import ru.astrainteractive.aspekt.asUnboxed
import ru.astrainteractive.aspekt.core.forge.coroutine.ForgeMainDispatcher
import ru.astrainteractive.aspekt.core.forge.event.flowEvent
import ru.astrainteractive.aspekt.core.forge.event.playerMoveFlowEvent
import ru.astrainteractive.aspekt.core.forge.util.asAudience
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.minecraft.location.dist
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue

class ForgeAuthEvent(
    private val authorizedApi: AuthorizedApi,
    private val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    translationKrate: CachedKrate<AuthTranslation>
) : KyoriComponentSerializer by kyoriKrate.asUnboxed() {
    val translation by translationKrate
    private val scope = CoroutineScope(SupervisorJob() + ForgeMainDispatcher) // todo

    val playerLoggedOutEvent = flowEvent<PlayerLoggedOutEvent>()
        .onEach { authorizedApi.forgetUser(it.entity.uuid) }
        .launchIn(scope)

    val playerLoggedInEvent = flowEvent<PlayerLoggedInEvent>()
        .onEach {
            val playerLoginModel = PlayerLoginModel(
                username = it.entity.name.toPlain(),
                uuid = it.entity.uuid,
                ip = (it.entity as ServerPlayer).ipAddress
            )
            authorizedApi.loadUserInfo(playerLoginModel)
        }
        .launchIn(scope)

    private fun processPlayerEvent(player: Player) = scope.launch {
        when (authorizedApi.getAuthState(player.uuid)) {
            AuthorizedApi.AuthState.Authorized -> Unit

            AuthorizedApi.AuthState.Pending,
            AuthorizedApi.AuthState.NotAuthorized -> {
                player
                    .asAudience()
                    .sendMessage(translation.notAuthorized.component)
            }

            AuthorizedApi.AuthState.NotRegistered -> {
                player
                    .asAudience()
                    .sendMessage(translation.notRegistered.component)
            }
        }
    }

    val playerMoveEvent = playerMoveFlowEvent()
        .filter { event -> authorizedApi.getAuthState(event.player.uuid) !is AuthorizedApi.AuthState.Authorized }
        .onEach { event ->
            if (event.newLocation.dist(event.oldLocation) > 0.001) {
                processPlayerEvent(event.player)
                event.player.teleportTo(
                    event.oldLocation.x,
                    event.oldLocation.y,
                    event.oldLocation.z
                )
            }
        }.launchIn(scope)

    val breakEvent = flowEvent<BlockEvent.BreakEvent>(EventPriority.HIGHEST)
        .filter { it.player.isAlive }
        .filter { authorizedApi.getAuthState(it.player.uuid) !is AuthorizedApi.AuthState.Authorized }
        .onEach { event ->
            println(event)
            event.isCanceled = true
            processPlayerEvent(event.player)
        }.launchIn(scope)

    val playerEvent = flowEvent<EntityEvent>(EventPriority.HIGHEST)
        .filter { it.isCancelable }
        .filter { it.entity is Player }
        .filter { it !is LivingEvent.LivingTickEvent }
        .filter { it !is EntityJoinLevelEvent }
        .filter { it !is PlayerLoggedInEvent }
        .filter { it.entity.isAlive }
        .filter { authorizedApi.getAuthState(it.entity.uuid) !is AuthorizedApi.AuthState.Authorized }
        .onEach { event ->
            event.isCanceled = true
            processPlayerEvent(event.entity as Player)
        }
        .launchIn(scope)
}
