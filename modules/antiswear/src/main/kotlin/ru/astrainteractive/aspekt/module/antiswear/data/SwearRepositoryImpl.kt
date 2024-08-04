package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.StringFormat
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.model.AntiSwearStorage
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.util.KrateExt.update
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File
import java.util.UUID

internal class SwearRepositoryImpl(
    private val dispatchers: KotlinDispatchers,
    private val tempFileStringFormat: StringFormat,
    private val folder: File = File("./.temp/antiswear"),
) : SwearRepository {
    private val swearFilterMap = mutableMapOf<UUID, Boolean>()

    private fun getAntiSwearKrate(
        player: Player
    ): SuspendMutableKrate<AntiSwearStorage> = AntiSwearKrate(
        player = player,
        stringFormat = tempFileStringFormat,
        folder = folder
    )

    override suspend fun rememberPlayer(player: Player) = withContext(dispatchers.IO) {
        swearFilterMap[player.uniqueId] = getAntiSwearKrate(player).loadAndGet().isSwearFilterEnabled
    }

    override suspend fun forgetPlayer(player: Player): Unit = withContext(dispatchers.IO) {
        swearFilterMap.remove(player.uniqueId)
    }

    override fun isSwearFilterEnabled(player: Player): Boolean {
        return swearFilterMap[player.uniqueId] ?: true
    }

    override suspend fun setSwearFilterEnabled(player: Player, isEnabled: Boolean) = withContext(dispatchers.IO) {
        swearFilterMap[player.uniqueId] = isEnabled
        getAntiSwearKrate(player).update { value ->
            value.copy(isSwearFilterEnabled = isEnabled)
        }
    }
}
