package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.antiswear.data.krate.AntiSwearKrate
import ru.astrainteractive.aspekt.module.antiswear.data.model.AntiSwearStorage
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.util.save
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.io.File
import java.util.UUID

internal class SwearRepositoryImpl(
    private val dispatchers: KotlinDispatchers,
    private val tempFileStringFormat: StringFormat,
    private val folder: File = File("./.temp/antiswear"),
) : SwearRepository, Logger by JUtiltLogger("AspeKt-SwearRepositoryImpl") {
    private val swearFilterMap = mutableMapOf<UUID, Boolean>()

    private fun getAntiSwearKrate(
        player: OnlineKPlayer
    ): SuspendMutableKrate<AntiSwearStorage> = AntiSwearKrate(
        kPlayer = player,
        stringFormat = tempFileStringFormat,
        folder = folder
    )

    override suspend fun rememberPlayer(player: OnlineKPlayer) = withContext(dispatchers.IO) {
        swearFilterMap[player.uuid] = getAntiSwearKrate(player).getValue().isSwearFilterEnabled
    }

    override suspend fun forgetPlayer(player: OnlineKPlayer): Unit = withContext(dispatchers.IO) {
        swearFilterMap.remove(player.uuid)
    }

    override fun isSwearFilterEnabled(player: OnlineKPlayer): Boolean {
        val value = swearFilterMap[player.uuid] ?: true
        return value
    }

    override suspend fun setSwearFilterEnabled(player: OnlineKPlayer, isEnabled: Boolean) {
        return withContext(dispatchers.IO) {
            swearFilterMap[player.uuid] = isEnabled
            getAntiSwearKrate(player).save { value ->
                value.copy(isSwearFilterEnabled = isEnabled)
            }
        }
    }

    override fun clear() {
        swearFilterMap.clear()
    }
}
