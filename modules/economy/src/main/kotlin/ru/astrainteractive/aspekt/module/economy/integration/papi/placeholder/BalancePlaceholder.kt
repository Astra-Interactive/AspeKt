package ru.astrainteractive.aspekt.module.economy.integration.papi.placeholder

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.integration.papi.core.Placeholder
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

/**
 * aspekt_balance_<currency>_<player>
 */
internal class BalancePlaceholder(
    private val dao: EconomyDao,
    private val scope: CoroutineScope
) : Placeholder, Logger by JUtiltLogger("BalancePlaceholder") {
    override val key: String = "balance"

    private data class PlayerInfo(
        val uuid: UUID,
        val currencyId: String
    )

    private val allCurrenciesCache = Cache.Builder<PlayerInfo, Double>()
        .maximumCacheSize(1024)
        .expireAfterWrite(30.seconds)
        .expireAfterAccess(30.seconds)
        .build()

    override fun asPlaceholder(executor: OfflinePlayer?, params: List<String>): String {
        val currencyId = params.getOrNull(2) ?: return null.orEmpty()
        val offlinePlayer = params.getOrNull(3)
            ?.let(Bukkit::getOfflinePlayer)
            ?: executor
            ?: return null.orEmpty()
        val playerInfo = PlayerInfo(
            currencyId = currencyId,
            uuid = offlinePlayer.uniqueId
        )
        val cachedAmount = allCurrenciesCache.get(playerInfo)
        if (cachedAmount == null) {
            scope.launch {
                val currency = dao.findPlayerCurrency(playerInfo.uuid.toString(), playerInfo.currencyId)
                allCurrenciesCache.put(playerInfo, currency?.balance ?: 0.0)
            }
        }
        return "${cachedAmount ?: 0.0}"
    }
}
