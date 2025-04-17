package ru.astrainteractive.aspekt.module.sethome.data

import com.google.common.cache.CacheBuilder
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.sethome.data.krate.PlayerHomesKrate
import java.io.File
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class HomeKrateProvider(
    private val folder: File,
    private val stringFormat: StringFormat,
) {
    private val cache = CacheBuilder<UUID, PlayerHomesKrate>
        .newBuilder()
        .expireAfterAccess(60.seconds.toJavaDuration())
        .build<UUID, PlayerHomesKrate>()

    fun get(uuid: UUID): PlayerHomesKrate {
        return cache.get(uuid) {
            PlayerHomesKrate(
                folder = folder,
                stringFormat = stringFormat,
                uuid = uuid
            )
        }
    }

    fun clear() {
        cache.cleanUp()
    }
}
