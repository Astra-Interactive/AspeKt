package ru.astrainteractive.aspekt.module.jail.data

import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate

internal interface JailApi {
    /**
     * Delete jail and all it's inmates
     */
    suspend fun deleteJail(jailName: String): Result<Unit>

    /**
     * Create new jail
     */
    suspend fun addJail(jail: Jail): Result<Unit>

    /**
     * Fetch all jails
     */
    suspend fun getJails(): Result<List<Jail>>

    /**
     * get specific jail
     */
    suspend fun getJail(name: String): Result<Jail>

    /**
     * Get current jail inmates
     */
    suspend fun getJailInmates(jailName: String): Result<List<JailInmate>>
    suspend fun getInmates(): Result<List<JailInmate>>

    /**
     * Add player into jail cell
     */
    suspend fun addInmate(inmate: JailInmate): Result<Unit>

    /**
     * Find jail inmate
     */
    suspend fun getInmate(uuid: String): Result<JailInmate>

    /**
     * Free inmate from jail
     */
    suspend fun free(uuid: String): Result<Unit>
}
