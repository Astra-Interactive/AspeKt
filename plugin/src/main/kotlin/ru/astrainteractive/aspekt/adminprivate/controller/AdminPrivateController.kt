package ru.astrainteractive.aspekt.adminprivate.controller

import ru.astrainteractive.aspekt.adminprivate.controller.di.AdminPrivateControllerModule
import ru.astrainteractive.aspekt.adminprivate.models.AdminChunk
import ru.astrainteractive.aspekt.adminprivate.models.ChunkFlag
import ru.astrainteractive.astralibs.async.AsyncComponent

class AdminPrivateController(module: AdminPrivateControllerModule) :
    AsyncComponent(),
    AdminPrivateControllerModule by module {

    suspend fun claim(adminChunk: AdminChunk) {
        repository.saveChunk(adminChunk)
    }

    suspend fun unclaim(adminChunk: AdminChunk) {
        repository.deleteChunk(adminChunk)
    }

    suspend fun setFlag(flag: ChunkFlag, value: Boolean, chunk: AdminChunk) {
        val actualChunk = repository.getChunk(chunk)
        val updatedChunk = actualChunk.copy(
            flags = actualChunk.flags.toMutableMap().apply {
                this[flag] = value
            }
        )
        repository.saveChunk(updatedChunk)
    }
}
