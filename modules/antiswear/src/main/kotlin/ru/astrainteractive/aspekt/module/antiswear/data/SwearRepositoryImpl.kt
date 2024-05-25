package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.coroutines.withContext
import kotlinx.serialization.StringFormat
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.model.AntiSwearStorage
import ru.astrainteractive.klibs.kstorage.api.MutableStorageValue
import ru.astrainteractive.klibs.kstorage.update
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal class SwearRepositoryImpl(
    private val dispatchers: KotlinDispatchers,
    private val tempFileStringFormat: StringFormat,
) : SwearRepository {
    private val swearFilterMap = mutableMapOf<UUID, Boolean>()

    private suspend fun getAntiSwearKrate(
        player: Player
    ): MutableStorageValue<AntiSwearStorage> = withContext(dispatchers.IO) {
        AntiSwearKrate(player = player, stringFormat = tempFileStringFormat)
    }

    override suspend fun rememberPlayer(player: Player) = withContext(dispatchers.IO) {
        swearFilterMap[player.uniqueId] = getAntiSwearKrate(player).value.isSwearFilterEnabled
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
