@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.claims.api.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class SetChunkFlagUseCaseTest {
    @Test
    fun GIVEN_claimed_chunk_WHEN_flag_is_set_THEN_chunk_keeps_the_flag_value(): TestResult {
        return runTest {
            val repository = ClaimTestData.createRepository(this)
            val claimChunkUseCase = ClaimChunkUseCase(claimsRepository = repository)
            val setChunkFlagUseCase = SetChunkFlagUseCase(claimsRepository = repository)
            ClaimTestData.randomChunk.let { chunk ->
                claimChunkUseCase.invoke(ClaimTestData.claimPlayer.uuid, chunk)
                setChunkFlagUseCase.invoke(
                    uuid = ClaimTestData.claimPlayer.uuid,
                    flag = ChunkFlag.ALLOW_BREAK,
                    value = true,
                    key = chunk.uniqueWorldKey
                )
                repository.loadAllChunks().first().flags[ChunkFlag.ALLOW_BREAK].let { flagValue ->
                    assertNotNull(flagValue)
                    assertTrue(flagValue)
                }
                setChunkFlagUseCase.invoke(
                    uuid = ClaimTestData.claimPlayer.uuid,
                    flag = ChunkFlag.ALLOW_BREAK,
                    value = false,
                    key = chunk.uniqueWorldKey
                )
                repository.loadAllChunks().first().flags[ChunkFlag.ALLOW_BREAK].let { flagValue ->
                    assertNotNull(flagValue)
                    assertFalse(flagValue)
                }
            }
        }
    }
}
