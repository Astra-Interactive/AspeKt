@file:Suppress("FieldDependencyConstruction")

package ru.astrainteractive.aspekt.module.sethome.krate

import com.google.common.cache.CacheBuilder
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.sethome.api.PlayerHomesRepository
import ru.astrainteractive.aspekt.module.sethome.model.PlayerHome
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendMutableKrate
import java.io.File
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

internal class KratePlayerHomesRepository(
    private val folder: File,
    private val stringFormat: StringFormat,
) : PlayerHomesRepository {
    private val cache = CacheBuilder<UUID, PlayerHomesKrate>.newBuilder()
        .expireAfterAccess(60.seconds.toJavaDuration())
        .build<UUID, PlayerHomesKrate>()

    override fun homesOf(uuid: UUID): StateFlowSuspendMutableKrate<List<PlayerHome>> {
        return cache.get(uuid) {
            PlayerHomesKrate(
                folder = folder,
                stringFormat = stringFormat,
                uuid = uuid
            )
        }
    }

    override fun clear() {
        cache.invalidateAll()
    }
}
