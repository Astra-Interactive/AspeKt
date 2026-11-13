@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.claims.api.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey
import ru.astrainteractive.klibs.mikro.core.coroutines.awaitForCompletion
import kotlin.test.assertEquals

internal class ClaimChunkUseCaseTest {
    @Test
    fun GIVEN_free_chunk_WHEN_claimed_and_deleted_THEN_chunk_count_follows(): TestResult {
        return runTest {
            val repository = ClaimTestData.createRepository(this)
            val claimChunkUseCase = ClaimChunkUseCase(claimsRepository = repository)
            ClaimTestData.randomChunk.let { chunk ->
                assertEquals(0, repository.loadAllChunks().size)
                claimChunkUseCase.invoke(ClaimTestData.claimPlayer.uuid, chunk)
                assertEquals(1, repository.loadAllChunks().size)
                awaitForCompletion { chunk.uniqueWorldKey in repository.chunkByKrate }
                repository.deleteChunk(ClaimTestData.claimPlayer.uuid, chunk.uniqueWorldKey)
                assertEquals(0, repository.loadAllChunks().size)
            }
        }
    }
}
