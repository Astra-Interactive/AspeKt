package ru.astrainteractive.aspekt.module.economy.integration.papi.expansion

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.model.CurrencyId
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

/**
 * aspekt_balance_<currency>_<player>
 */
internal class BalancePlaceholder(
    private val repository: EconomyRepository,
    private val scope: CoroutineScope
) : Placeholder, Logger by JUtiltLogger("BalancePlaceholder") {
    override val key: String = "balance"

    private data class PlayerInfo(
        val uuid: UUID,
        val currencyId: CurrencyId
    )

    @Suppress("MagicNumber")
    private val allCurrenciesCache = Cache.Builder<PlayerInfo, Double>()
        .maximumCacheSize(1024)
        .expireAfterWrite(30.seconds)
        .expireAfterAccess(30.seconds)
        .build()

    @Suppress("MagicNumber")
    override fun asPlaceholder(executor: OfflinePlayer?, params: List<String>): String {
        val currencyId = params.getOrNull(2) ?: return null.orEmpty()
        val offlinePlayer = params.getOrNull(3)
            ?.let(Bukkit::getOfflinePlayer)
            ?: executor
            ?: return null.orEmpty()
        val playerInfo = PlayerInfo(
            currencyId = CurrencyId(currencyId),
            uuid = offlinePlayer.uniqueId
        )
        val cachedAmount = allCurrenciesCache.get(playerInfo)
        if (cachedAmount == null) {
            scope.launch {
                val currency = repository.findPlayerCurrency(playerInfo.uuid.toString(), playerInfo.currencyId.value)
                allCurrenciesCache.put(playerInfo, currency?.balance ?: 0.0)
            }
        }
        return "${cachedAmount ?: 0.0}"
    }
}
