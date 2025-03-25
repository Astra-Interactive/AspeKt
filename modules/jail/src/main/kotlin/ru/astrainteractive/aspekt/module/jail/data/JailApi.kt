package ru.astrainteractive.aspekt.module.jail.data

import kotlin.uuid.Uuid
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
     * Get current jail inmates
     */
    suspend fun getJailInmates(jailName: String): Result<List<JailInmate>>

    /**
     * Add player into jail cell
     */
    suspend fun addInmate(inmate: JailInmate): Result<Unit>

    /**
     * Free inmate from jail
     */
    suspend fun free(uuid: String): Result<Unit>
}
