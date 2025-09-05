package ru.astrainteractive.aspekt.module.claims.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.storage.ServerLevelData
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.event.level.ExplosionEvent
import net.minecraftforge.eventbus.api.Event
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
import ru.astrainteractive.aspekt.module.claims.data.isAble
import ru.astrainteractive.aspekt.module.claims.debounce.EventDebounce
import ru.astrainteractive.aspekt.module.claims.debounce.RetractKey
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.util.toClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.coroutine.ForgeMainDispatcher
import ru.astrainteractive.astralibs.event.flowEvent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.server.util.asPermissible
import ru.astrainteractive.astralibs.server.util.toNative
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

class ForgeClaimEvent(
    private val claimsRepository: ClaimsRepository,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>
) : Logger by JUtiltLogger("AspeKt-ForgeClaimEvent") {
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
    ): Boolean where T : Event {
        if (player?.asPermissible()?.hasPermission(PluginPermission.ADMIN_CLAIM) == true) {
            return false
        }
        if (e.isCanceled) {
            return true
        }
        val sharedEvent = if (e.isCancelable) ForgeSharedCancellableEvent(e) else ForgeEmptyCancellableEvent()
        return debounce.debounceEvent(retractKey, sharedEvent) {
            val isAble = claimsRepository.isAble(
                key = claimChunk.uniqueWorldKey,
                chunkFlag = flag,
                claimPlayer = player?.toClaimPlayer()
            )
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
                flag = ChunkFlag.ALLOW_BREAK
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
                flag = ChunkFlag.ALLOW_PLACE
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
                flag = ChunkFlag.ALLOW_INTERACT
            )
        }.launchIn(scope)

    val explodeEvent = flowEvent<ExplosionEvent>()
        .onEach { e ->
            val blocksPos = if (e is ExplosionEvent.Detonate) {
                e.affectedBlocks
            } else {
                listOf(
                    BlockPos(
                        e.explosion.position.x.toInt(),
                        e.explosion.position.y.toInt(),
                        e.explosion.position.z.toInt()
                    )
                )
            }
            val isCancelled = blocksPos
                .map { blockPos -> e.level.getChunkAt(blockPos) }
                .map { chunk -> chunk.toClaimChunk(e.level) }
                .distinct()
                .map { claimChunk ->
                    handleDefault(
                        retractKey = RetractKey.Vararg(
                            claimChunk.x,
                            claimChunk.z,
                            "ExplosionEvent"
                        ),
                        e = e,
                        claimChunk = claimChunk,
                        player = null,
                        flag = ChunkFlag.ALLOW_EXPLODE
                    )
                }.any()
            if (isCancelled && e is ExplosionEvent.Detonate) {
                e.affectedBlocks.clear()
                e.affectedEntities.clear()
            }
        }.launchIn(scope)
}
