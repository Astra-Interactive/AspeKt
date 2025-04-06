package ru.astrainteractive.aspekt.module.adminprivate.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.aspekt.module.adminprivate.util.uniqueWorldKey
import ru.astrainteractive.astralibs.async.CoroutineFeature

class AdminPrivateController(
    dependencies: AdminPrivateControllerDependencies
) : CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO),
    AdminPrivateControllerDependencies by dependencies {

    val isEnabled: Boolean
        get() = repository.krate.cachedValue.isEnabled

    fun reloadKrate() = launch { repository.krate.loadAndGet() }

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
            flags = ChunkFlag.entries.associateWith { false }
        )
        repository.saveChunk(actualAdminChunk)
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

    fun isAble(chunk: AdminChunk, chunkFlag: ChunkFlag): Boolean {
        val actualChunk = repository.krate.cachedValue.chunks[chunk.uniqueWorldKey] ?: return true
        return actualChunk.flags[chunkFlag] ?: false
    }
}
