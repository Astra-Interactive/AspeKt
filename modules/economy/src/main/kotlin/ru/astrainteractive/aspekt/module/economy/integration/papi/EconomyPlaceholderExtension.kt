package ru.astrainteractive.aspekt.module.economy.integration.papi

import kotlinx.coroutines.CoroutineScope
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.integration.papi.core.Placeholder
import ru.astrainteractive.aspekt.module.economy.integration.papi.placeholder.BalancePlaceholder
import ru.astrainteractive.astralibs.expansion.KPlaceholderExpansion

internal class EconomyPlaceholderExtension(
    dao: EconomyDao,
    scope: CoroutineScope
) : KPlaceholderExpansion(
    "aspekt",
    "RomanMakeev",
    "1.0.0"
) {
    private val placeholders = listOf<Placeholder>(
        BalancePlaceholder(
            dao = dao,
            scope = scope
        ),
    )

    /**
     * erating_RomaRoman
     * erating_rating
     */
    override fun onRequest(player: OfflinePlayer, params: String): String {
        val args = params.split("_")
        val placeholder = placeholders.firstOrNull { it.key == args.getOrNull(1) }
        return placeholder?.asPlaceholder(player, args).orEmpty()
    }
}
