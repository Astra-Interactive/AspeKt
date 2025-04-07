package ru.astrainteractive.aspekt.module.auth.event

import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
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
import ru.astrainteractive.aspekt.core.forge.coroutine.ForgeMainDispatcher
import ru.astrainteractive.aspekt.core.forge.event.flowEvent
import ru.astrainteractive.aspekt.core.forge.kyori.sendSystemMessage
import ru.astrainteractive.aspekt.core.forge.kyori.withAudience
import ru.astrainteractive.aspekt.core.forge.util.toPlain
import ru.astrainteractive.aspekt.module.auth.api.AuthorizedApi
import ru.astrainteractive.aspekt.module.auth.api.model.PlayerLoginModel
import ru.astrainteractive.aspekt.module.auth.api.plugin.AuthTranslation
import ru.astrainteractive.aspekt.module.auth.event.model.Location
import ru.astrainteractive.aspekt.module.auth.event.model.dist
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.CacheOwnerExt.getValue
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class ForgeAuthEvent(
    private val authorizedApi: AuthorizedApi,
    private val kyoriKrate: Krate<KyoriComponentSerializer>,
    translationKrate: Krate<AuthTranslation>
) {
    val translation by translationKrate
    private val scope = CoroutineScope(SupervisorJob() + ForgeMainDispatcher)

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
                kyoriKrate
                    .withAudience(player)
                    .sendSystemMessage(translation.notAuthorized)
            }

            AuthorizedApi.AuthState.NotRegistered -> {
                kyoriKrate
                    .withAudience(player)
                    .sendSystemMessage(translation.notRegistered)
            }
        }
    }

    val playerMoveEvent = flow {
        val cache = CacheBuilder<UUID, Location>
            .newBuilder()
            .expireAfterAccess(10.seconds.toJavaDuration())
            .build<UUID, Location>()
        flowEvent<LivingEvent.LivingTickEvent>()
            .filter { it.entity.isAlive }
            .filter { it.entity is Player }
            .filter { authorizedApi.getAuthState(it.entity.uuid) !is AuthorizedApi.AuthState.Authorized }
            .onEach {
                val location = Location(
                    x = it.entity.x,
                    y = it.entity.y,
                    z = it.entity.z
                )
                val cachedLocation = cache.get(it.entity.uuid) {
                    location.copy(
                        x = location.x,
                        y = location.y,
                        z = location.z,
                    )
                }
                if (location.dist(cachedLocation) > 0.001) {
                    processPlayerEvent(it.entity as Player)
                    it.entity.teleportTo(
                        cachedLocation.x,
                        cachedLocation.y,
                        cachedLocation.z
                    )
                    emit(Unit)
                } else {
                    cache.put(
                        it.entity.uuid,
                        location.copy(
                            x = location.x,
                            y = location.y,
                            z = location.z,
                        )
                    )
                }
            }.collect()
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
