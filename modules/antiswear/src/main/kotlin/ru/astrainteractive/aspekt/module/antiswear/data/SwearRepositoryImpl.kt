package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import ru.astrainteractive.aspekt.module.antiswear.model.AntiSwearStorage
import ru.astrainteractive.astralibs.krate.Krate
import ru.astrainteractive.astralibs.krate.KrateExt.create
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.UUID

internal class SwearRepositoryImpl(
    private val dispatchers: KotlinDispatchers,
) : SwearRepository {
    private val swearFilterMap = mutableMapOf<UUID, Boolean>()

    private suspend fun getAntiSwearStorageValue(
        player: Player
    ): Krate<AntiSwearStorage> = withContext(dispatchers.IO) {
        AntiSwearKrateFactory.create(
            key = player.uniqueId.toString(),
            default = {
                AntiSwearStorage(
                    playerName = player.name,
                    uuid = player.uniqueId.toString(),
                )
            }
        )
    }

    override suspend fun rememberPlayer(player: Player) = withContext(dispatchers.IO) {
        swearFilterMap[player.uniqueId] = getAntiSwearStorageValue(player).value.isSwearFilterEnabled
    }

    override suspend fun forgetPlayer(player: Player): Unit = withContext(dispatchers.IO) {
        swearFilterMap.remove(player.uniqueId)
    }

    override fun isSwearFilterEnabled(player: Player): Boolean {
        return swearFilterMap[player.uniqueId] ?: true
    }

    override suspend fun setSwearFilterEnabled(player: Player, isEnabled: Boolean) = withContext(dispatchers.IO) {
        swearFilterMap[player.uniqueId] = isEnabled
        getAntiSwearStorageValue(player).update { value ->
            value.copy(isSwearFilterEnabled = isEnabled)
        }
    }
}
