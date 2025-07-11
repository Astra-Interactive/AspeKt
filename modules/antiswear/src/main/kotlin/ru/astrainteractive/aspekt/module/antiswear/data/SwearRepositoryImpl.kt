package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.StringFormat
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.data.krate.AntiSwearKrate
import ru.astrainteractive.aspekt.module.antiswear.data.model.AntiSwearStorage
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kstorage.suspend.SuspendMutableKrate
import ru.astrainteractive.klibs.kstorage.util.update
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.io.File
import java.util.UUID

internal class SwearRepositoryImpl(
    private val dispatchers: KotlinDispatchers,
    private val tempFileStringFormat: StringFormat,
    private val folder: File = File("./.temp/antiswear"),
) : SwearRepository, Logger by JUtiltLogger("AspeKt-SwearRepositoryImpl") {
    private val swearFilterMap = mutableMapOf<UUID, Boolean>()

    private fun getAntiSwearKrate(
        player: Player
    ): SuspendMutableKrate<AntiSwearStorage> = AntiSwearKrate(
        player = player,
        stringFormat = tempFileStringFormat,
        folder = folder
    )

    override suspend fun rememberPlayer(player: Player) = withContext(dispatchers.IO) {
        swearFilterMap[player.uniqueId] = getAntiSwearKrate(player).getValue().isSwearFilterEnabled
    }

    override suspend fun forgetPlayer(player: Player): Unit = withContext(dispatchers.IO) {
        swearFilterMap.remove(player.uniqueId)
    }

    override fun isSwearFilterEnabled(player: Player): Boolean {
        val value = swearFilterMap[player.uniqueId] ?: true
        return value
    }

    override suspend fun setSwearFilterEnabled(player: Player, isEnabled: Boolean) = withContext(dispatchers.IO) {
        swearFilterMap[player.uniqueId] = isEnabled
        getAntiSwearKrate(player).update { value ->
            value.copy(isSwearFilterEnabled = isEnabled)
        }
    }

    override fun clear() {
        swearFilterMap.clear()
    }
}
