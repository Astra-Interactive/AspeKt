package ru.astrainteractive.aspekt.module.jail.api

import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate

internal interface JailRepository {
    suspend fun deleteJail(jailName: String): Result<Unit>

    suspend fun addJail(jail: Jail): Result<Unit>

    suspend fun findJails(): Result<List<Jail>>

    suspend fun findJail(name: String): Result<Jail>

    suspend fun findJailInmates(jailName: String): Result<List<JailInmate>>
    suspend fun findInmates(): Result<List<JailInmate>>

    suspend fun addInmate(inmate: JailInmate): Result<Unit>

    suspend fun findInmate(uuid: String): Result<JailInmate>

    suspend fun free(uuid: String): Result<Unit>
}
