package ru.astrainteractive.aspekt.module.economy.integration.papi.expansion

import org.bukkit.OfflinePlayer

internal interface Placeholder {
    val key: String
    fun asPlaceholder(param: OfflinePlayer?, params: List<String>): String
}
