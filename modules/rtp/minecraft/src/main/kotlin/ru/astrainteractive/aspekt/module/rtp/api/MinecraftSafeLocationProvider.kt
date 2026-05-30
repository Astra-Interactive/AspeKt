package ru.astrainteractive.aspekt.module.rtp.api

import kotlinx.coroutines.CoroutineScope
import net.minecraft.server.level.ServerLevel
import ru.astrainteractive.aspekt.module.rtp.model.RtpConfig
import ru.astrainteractive.aspekt.module.rtp.search.RtpCooldownRegistry
import ru.astrainteractive.aspekt.module.rtp.search.RtpSearchJobRegistry
import ru.astrainteractive.aspekt.module.rtp.search.SafeLocationSearcher
import ru.astrainteractive.astralibs.server.util.MinecraftUtil
import ru.astrainteractive.astralibs.server.util.getNextTickTime
import ru.astrainteractive.astralibs.server.util.getOnlinePlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.util.cast
import java.util.UUID

class MinecraftSafeLocationProvider(
    dispatchers: KotlinDispatchers,
    private val rtpConfigKrate: CachedKrate<RtpConfig>,
) : SafeLocationProvider {
    private val jobRegistry: RtpSearchJobRegistry = RtpSearchJobRegistry()
    private val cooldownRegistry: RtpCooldownRegistry = RtpCooldownRegistry()
    private val searcher: SafeLocationSearcher = SafeLocationSearcher(dispatchers)

    override suspend fun getLocation(ioScope: CoroutineScope, uuid: UUID): RtpSearchResult {
        val player = MinecraftUtil.getOnlinePlayer(uuid) ?: return RtpSearchResult.NotFound
        return jobRegistry.await(uuid, ioScope) {
            searcher.findSafeLocation(
                level = player.level().cast<ServerLevel>(),
                config = rtpConfigKrate.cachedValue,
            )
        }
    }

    override fun getJobsNumber(): Int = jobRegistry.count()

    override fun isActive(uuid: UUID): Boolean = jobRegistry.isActive(uuid)

    override fun hasTimeout(uuid: UUID): Boolean = cooldownRegistry.hasTimeout(uuid)

    override fun getNextTickTime(): Double = MinecraftUtil.getNextTickTime()
}
