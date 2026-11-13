@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.claims.api.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.astrainteractive.aspekt.module.claims.api.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.api.util.uniqueWorldKey
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CheckChunkActionUseCaseTest {
    @Test
    fun GIVEN_chunk_WHEN_claimed_and_flag_changes_THEN_action_permission_follows(): TestResult {
        return runTest {
            val repository = ClaimTestData.createRepository(this)
            val claimChunkUseCase = ClaimChunkUseCase(claimsRepository = repository)
            val setChunkFlagUseCase = SetChunkFlagUseCase(claimsRepository = repository)
            val checkChunkActionUseCase = CheckChunkActionUseCase(claimsRepository = repository)
            ClaimTestData.randomChunk.let { chunk ->
                assertTrue { checkChunkActionUseCase.invoke(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
                claimChunkUseCase.invoke(ClaimTestData.claimPlayer.uuid, chunk)
                assertFalse { checkChunkActionUseCase.invoke(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
                setChunkFlagUseCase.invoke(
                    uuid = ClaimTestData.claimPlayer.uuid,
                    flag = ChunkFlag.ALLOW_BREAK,
                    value = true,
                    key = chunk.uniqueWorldKey
                )
                assertTrue { checkChunkActionUseCase.invoke(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
                setChunkFlagUseCase.invoke(
                    uuid = ClaimTestData.claimPlayer.uuid,
                    flag = ChunkFlag.ALLOW_BREAK,
                    value = false,
                    key = chunk.uniqueWorldKey
                )
                assertFalse { checkChunkActionUseCase.invoke(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
            }
        }
    }
}
