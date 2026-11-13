@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.claims.api.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertContentEquals

internal class CreateClaimGridUseCaseTest {
    @Test
    fun GIVEN_claimed_chunk_WHEN_mapped_with_size_three_THEN_only_center_is_claimed(): TestResult {
        return runTest {
            val repository = ClaimTestData.createRepository(this)
            val claimChunkUseCase = ClaimChunkUseCase(claimsRepository = repository)
            val createClaimGridUseCase = CreateClaimGridUseCase(claimsRepository = repository)
            ClaimTestData.randomChunk.let { chunk ->
                claimChunkUseCase.invoke(ClaimTestData.claimPlayer.uuid, chunk)
                val expectArray = listOf(
                    listOf(
                        false,
                        false,
                        false
                    ),
                    listOf(
                        false,
                        true,
                        false
                    ),
                    listOf(
                        false,
                        false,
                        false
                    )
                )
                assertContentEquals(expectArray, createClaimGridUseCase.invoke(3, chunk))
            }
        }
    }
}
