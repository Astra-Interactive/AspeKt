package ru.astrainteractive.aspekt.module.economy.integration.papi

import kotlinx.coroutines.CoroutineScope
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.integration.papi.core.Placeholder
import ru.astrainteractive.aspekt.module.economy.integration.papi.placeholder.BalancePlaceholder
import ru.astrainteractive.astralibs.expansion.KPlaceholderExpansion

internal class EconomyPlaceholderExtension(
    repository: EconomyRepository,
    scope: CoroutineScope
) : PlaceholderExpansionApi, KPlaceholderExpansion(
    "aspekt",
    "RomanMakeev",
    "1.0.0"
) {
    private val placeholders = listOf<Placeholder>(
        BalancePlaceholder(
            repository = repository,
            scope = scope
        ),
    )

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        val args = params.split("_")
        val placeholder = placeholders.firstOrNull { it.key == args.getOrNull(1) }
        return placeholder?.asPlaceholder(player, args).orEmpty()
    }
}
