package ru.astrainteractive.aspekt.module.jail.api

import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate

internal interface JailCache {
    fun isInJail(uuid: String): Boolean
    fun cache(uuid: String)
    fun forget(uuid: String)

    fun findJails(): List<Jail>
    fun findInmates(): List<JailInmate>
}
