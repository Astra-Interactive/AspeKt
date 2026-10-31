package ru.astrainteractive.aspekt.module.economy.integration.papi.api

interface PlaceholderRegistration {
    fun isRegistered(): Boolean

    fun register(): Boolean

    fun unregister(): Boolean
}
