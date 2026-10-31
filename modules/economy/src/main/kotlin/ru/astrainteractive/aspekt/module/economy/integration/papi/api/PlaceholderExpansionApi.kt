package ru.astrainteractive.aspekt.module.economy.integration.papi.api

interface PlaceholderExpansionApi {
    fun isRegistered(): Boolean

    fun register(): Boolean

    fun unregister(): Boolean
}
