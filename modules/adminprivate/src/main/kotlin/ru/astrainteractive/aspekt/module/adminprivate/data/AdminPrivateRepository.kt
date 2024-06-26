package ru.astrainteractive.aspekt.module.adminprivate.data

import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminPrivateConfig
import ru.astrainteractive.klibs.kstorage.suspend.SuspendKrate

internal interface AdminPrivateRepository {
    val krate: SuspendKrate.Mutable<AdminPrivateConfig>
    suspend fun getAllChunks(): List<AdminChunk>
    suspend fun getChunk(chunk: AdminChunk): AdminChunk
    suspend fun saveChunk(chunk: AdminChunk)
    suspend fun deleteChunk(chunk: AdminChunk)
}
