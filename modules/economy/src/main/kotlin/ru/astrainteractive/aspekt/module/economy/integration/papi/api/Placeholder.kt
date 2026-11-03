package ru.astrainteractive.aspekt.module.economy.integration.papi.api

import java.util.UUID

internal interface Placeholder {
    val key: String
    fun asPlaceholder(executorUuid: UUID?, params: List<String>): String
}
