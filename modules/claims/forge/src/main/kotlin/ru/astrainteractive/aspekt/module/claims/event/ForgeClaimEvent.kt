package ru.astrainteractive.aspekt.module.claims.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.storage.ServerLevelData
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.Event
import ru.astrainteractive.aspekt.core.forge.coroutine.ForgeMainDispatcher
import ru.astrainteractive.aspekt.core.forge.event.flowEvent
import ru.astrainteractive.aspekt.core.forge.permission.toPermissible
import ru.astrainteractive.aspekt.core.forge.util.getValue
import ru.astrainteractive.aspekt.core.forge.util.toNative
import ru.astrainteractive.aspekt.module.claims.controller.ClaimController
import ru.astrainteractive.aspekt.module.claims.debounce.EventDebounce
import ru.astrainteractive.aspekt.module.claims.debounce.RetractKey
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.Krate

class ForgeClaimEvent(
    private val claimController: ClaimController,
    translationKrate: Krate<PluginTranslation>,
    kyoriKrate: Krate<KyoriComponentSerializer>
) {
    private val translation by translationKrate
    private val kyori by kyoriKrate
    private val scope = CoroutineScope(SupervisorJob() + ForgeMainDispatcher)

    private val debounce = EventDebounce<RetractKey>(5000L)
    private fun <T> handleDefault(
        retractKey: RetractKey,
        e: T,
        claimChunk: ClaimChunk,
        player: ServerPlayer?,
        flag: ChunkFlag
    ) where T : Event {
        if (player?.toPermissible()?.hasPermission(PluginPermission.AdminClaim) == true) return
        if (e.isCanceled) return
        val sharedEvent = ForgeSharedCancellableEvent(e)
        debounce.debounceEvent(retractKey, sharedEvent) {
            val isAble = claimController.isAble(claimChunk, flag)
            val isCancelled = !isAble
            if (isCancelled && player != null) {
                translation.claim.actionIsBlockByAdminClaim(flag.name)
                    .let(kyori::toComponent)
                    .toNative()
                    .run(player::sendSystemMessage)
            }
            isCancelled
        }
    }

    private fun ChunkAccess.toClaimChunk(level: LevelAccessor): ClaimChunk {
        val serverLevel = level as ServerLevel
        val levelData = serverLevel.levelData as ServerLevelData
        val chunkKey = pos.x.toLong() and 0xffffffffL or ((pos.z.toLong() and 0xffffffffL) shl 32)
        return ClaimChunk(
            x = pos.x,
            z = pos.z,
            worldName = levelData.levelName,
            flags = emptyMap(),
            chunkKey = chunkKey
        )
    }

    val breakEvent = flowEvent<BlockEvent.BreakEvent>()
        .onEach { e ->
            val serverPlayer = e.player as? ServerPlayer ?: return@onEach
            val chunk = e.level.getChunk(e.pos)

            handleDefault(
                retractKey = RetractKey.Vararg(e.pos, serverPlayer, "breakEvent"),
                e = e,
                claimChunk = chunk.toClaimChunk(e.level),
                player = serverPlayer,
                flag = ChunkFlag.BREAK
            )
        }.launchIn(scope)

    val entityPlaceEvent = flowEvent<BlockEvent.EntityPlaceEvent>()
        .onEach { e ->
            val serverPlayer = e.entity as? ServerPlayer ?: return@onEach
            val chunk = e.level.getChunk(e.pos)

            handleDefault(
                retractKey = RetractKey.Vararg(e.pos, serverPlayer, "entityPlaceEvent"),
                e = e,
                claimChunk = chunk.toClaimChunk(e.level),
                player = serverPlayer,
                flag = ChunkFlag.PLACE
            )
        }.launchIn(scope)

    val playerInteractEvent = flowEvent<PlayerInteractEvent>()
        .onEach { e ->
            val serverPlayer = e.entity as? ServerPlayer ?: return@onEach
            val chunk = e.level.getChunk(e.pos)

            handleDefault(
                retractKey = RetractKey.Vararg(e.pos, serverPlayer, "playerInteractEvent"),
                e = e,
                claimChunk = chunk.toClaimChunk(e.level),
                player = serverPlayer,
                flag = ChunkFlag.INTERACT
            )
        }.launchIn(scope)
}
