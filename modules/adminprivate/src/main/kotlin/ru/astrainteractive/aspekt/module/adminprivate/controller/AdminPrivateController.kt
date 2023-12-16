package ru.astrainteractive.aspekt.module.adminprivate.controller

import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.module.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.getValue

class AdminPrivateController(module: AdminPrivateControllerDependencies) :
    AsyncComponent(),
    AdminPrivateControllerDependencies by module {
    private val chunks = Reloadable {
        repository.getConfig()
    }
    val isEnabled by Provider {
        chunks.value.isEnabled
    }

    fun updateChunks() = chunks.reload()

    suspend fun map(size: Int, chunk: AdminChunk): Array<Array<Boolean>> {
        val m = Array(size) {
            Array(size) { false }
        }
        val chunks = repository.getAllChunks()
        for (i in 0 until size) {
            for (j in 0 until size) {
                m[i][j] = chunks.any {
                    val x = chunk.x - size / 2 + i
                    val z = chunk.z + size / 2 - j
                    it.x == x && it.z == z
                }
            }
        }
        return m
    }

    suspend fun claim(adminChunk: AdminChunk) {
        val actualAdminChunk = adminChunk.copy(
            flags = ChunkFlag.values().associateWith { false }
        )
        repository.saveChunk(actualAdminChunk)
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
