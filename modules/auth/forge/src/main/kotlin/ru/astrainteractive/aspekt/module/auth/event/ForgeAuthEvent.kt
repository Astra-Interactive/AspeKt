package ru.astrainteractive.aspekt.module.auth.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.EventPriority
import ru.astrainteractive.aspekt.module.auth.api.AuthStateRepository
import ru.astrainteractive.aspekt.module.auth.model.AuthState
import ru.astrainteractive.aspekt.module.auth.model.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.model.PlayerLogin
import ru.astrainteractive.astralibs.event.flowEvent
import ru.astrainteractive.astralibs.event.playerMoveFlowEvent
import ru.astrainteractive.astralibs.server.location.dist
import ru.astrainteractive.astralibs.server.util.asKAudience
import ru.astrainteractive.astralibs.server.util.toPlain
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue

internal class ForgeAuthEvent(
    private val authStateRepository: AuthStateRepository,
    private val mainScope: CoroutineScope,
    translationKrate: CachedKrate<AuthTranslation>
) {
    val translation by translationKrate

    val playerLoggedOutEvent = flowEvent<PlayerEvent.PlayerLoggedOutEvent>()
        .onEach { event -> authStateRepository.forgetUser(event.entity.uuid) }
        .launchIn(mainScope)

    val playerLoggedInEvent = flowEvent<PlayerEvent.PlayerLoggedInEvent>()
        .onEach { event ->
            val playerLoginModel = PlayerLogin(
                username = event.entity.name.toPlain(),
                uuid = event.entity.uuid,
                ip = (event.entity as ServerPlayer).ipAddress
            )
            authStateRepository.loadUserInfo(playerLoginModel)
        }
        .launchIn(mainScope)

    @Suppress("MagicNumber")
    val playerMoveEvent = playerMoveFlowEvent()
        .filter { event -> authStateRepository.resolveAuthState(event.player.uuid) !is AuthState.Authorized }
        .onEach { event ->
            if (event.newKLocation.dist(event.oldKLocation) <= 0.001) return@onEach
            notifyPlayer(event.player)
            event.player.teleportTo(
                event.oldKLocation.x,
                event.oldKLocation.y,
                event.oldKLocation.z
            )
        }.launchIn(mainScope)

    val breakEvent = flowEvent<BlockEvent.BreakEvent>(EventPriority.HIGHEST)
        .filter { event -> event.player.isAlive }
        .filter { event -> authStateRepository.resolveAuthState(event.player.uuid) !is AuthState.Authorized }
        .onEach { event ->
            println(event)
            event.isCanceled = true
            notifyPlayer(event.player)
        }.launchIn(mainScope)

    val playerEvent = flowEvent<EntityEvent>(EventPriority.HIGHEST)
        .filter { event -> event.entity is Player }
        .filter { event -> event !is EntityJoinLevelEvent }
        .filter { event -> event !is PlayerEvent.PlayerLoggedInEvent }
        .filter { event -> event.entity.isAlive }
        .filter { event -> authStateRepository.resolveAuthState(event.entity.uuid) !is AuthState.Authorized }
        .onEach { event ->
            if (event.isCancelable) event.isCanceled = true
            notifyPlayer(event.entity as Player)
        }
        .launchIn(mainScope)

    private fun notifyPlayer(player: Player) {
        mainScope.launch {
            // Only a server-side player has a client to send the message to
            val serverPlayer = player as? ServerPlayer ?: return@launch
            try {
                when (authStateRepository.resolveAuthState(player.uuid)) {
                    AuthState.Authorized -> Unit

                    AuthState.Pending,
                    AuthState.NotAuthorized -> {
                        serverPlayer
                            .asKAudience()
                            .sendMessage(translation.notAuthorized)
                    }

                    AuthState.NotRegistered -> {
                        serverPlayer
                            .asKAudience()
                            .sendMessage(translation.notRegistered)
                    }
                }
            } catch (_: NullPointerException) {
            }
        }
    }
}
