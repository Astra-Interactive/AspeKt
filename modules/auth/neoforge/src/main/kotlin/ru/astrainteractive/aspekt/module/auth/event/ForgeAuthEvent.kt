package ru.astrainteractive.aspekt.module.auth.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.neoforge.common.util.TriState
import net.neoforged.neoforge.event.entity.EntityEvent
import net.neoforged.neoforge.event.entity.item.ItemTossEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.level.BlockEvent
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
import ru.astrainteractive.klibs.mikro.core.coroutines.merge
import ru.astrainteractive.klibs.mikro.core.util.tryCast

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

    val playerEvent = flowEvent<PlayerInteractEvent>(EventPriority.HIGHEST)
        .merge(flowEvent<AttackEntityEvent>(EventPriority.HIGHEST))
        .merge(flowEvent<LivingEntityUseItemEvent>(EventPriority.HIGHEST))
        .merge(flowEvent<ItemTossEvent>(EventPriority.HIGHEST))
        .merge(flowEvent<ItemEntityPickupEvent>(EventPriority.HIGHEST))
        .filter { event -> event.isPlayerInvolved()?.isAlive == true }
        .filter { event ->
            val player = event.isPlayerInvolved() ?: return@filter false
            authStateRepository.resolveAuthState(player.uuid) !is AuthState.Authorized
        }
        .onEach { event ->
            event.tryCast<ICancellableEvent>()?.isCanceled = true
            event.tryCast<ItemEntityPickupEvent.Pre>()?.setCanPickup(TriState.FALSE)
            event.isPlayerInvolved()?.let(::notifyPlayer)
        }
        .launchIn(mainScope)

    private fun notifyPlayer(player: Player) {
        mainScope.launch {
            // Only a server-side player has a client to send the message to
            val serverPlayer = player as? ServerPlayer ?: return@launch
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
        }
    }

    private fun Event.isPlayerInvolved(): Player? {
        return when (this) {
            is ItemTossEvent -> player
            is ItemEntityPickupEvent -> player
            is EntityEvent -> entity as? Player
            else -> null
        }
    }
}
