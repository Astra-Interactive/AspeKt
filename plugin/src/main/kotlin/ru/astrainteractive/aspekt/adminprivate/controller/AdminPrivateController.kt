package ru.astrainteractive.aspekt.adminprivate.controller

import ru.astrainteractive.aspekt.adminprivate.controller.di.AdminPrivateControllerModule
import ru.astrainteractive.aspekt.adminprivate.models.AdminChunk
import ru.astrainteractive.aspekt.adminprivate.models.ChunkFlag
import ru.astrainteractive.aspekt.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.klibs.kdi.Reloadable

class AdminPrivateController(module: AdminPrivateControllerModule) :
    AsyncComponent(),
    AdminPrivateControllerModule by module {
    private val chunks = Reloadable {
        repository.getConfig()
    }

    fun updateChunks() = chunks.reload()

    suspend fun claim(adminChunk: AdminChunk) {
        repository.saveChunk(adminChunk)
        updateChunks()
    }

    suspend fun unclaim(adminChunk: AdminChunk) {
        repository.deleteChunk(adminChunk)
        updateChunks()
    }

    suspend fun setFlag(flag: ChunkFlag, value: Boolean, chunk: AdminChunk) {
        val actualChunk = repository.getChunk(chunk)
        val updatedChunk = actualChunk.copy(
            flags = actualChunk.flags.toMutableMap().apply {
                this[flag] = value
            }
        )
        repository.saveChunk(updatedChunk)
        updateChunks()
    }

    fun isAble(chunk: AdminChunk, chunkFlag: ChunkFlag): Boolean {
        val actualChunk = chunks.value.chunks[chunk.uniqueWorldKey] ?: return true
        return actualChunk.flags[chunkFlag] ?: false
    }
}
