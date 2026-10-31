@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.claims.util

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.astrainteractive.aspekt.module.claims.krate.KrateClaimsRepository
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.util.YamlStringFormat
import ru.astrainteractive.klibs.mikro.core.coroutines.awaitForCompletion
import java.io.File
import java.util.UUID
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class ClaimsRepositoryExtTest {
    private val claimPlayer = ClaimPlayer(
        uuid = UUID.randomUUID(),
        username = "username"
    )
    private val randomChunk: ClaimChunk
        get() {
            val x = Random.nextInt(0, 100)
            val z = Random.nextInt(0, 100)
            return ClaimChunk(
                x = x,
                z = z,
                worldName = UUID.randomUUID().toString(),
                flags = emptyMap(),
                chunkKey = "$x$z".toLong()
            )
        }
    private val tempFile: File
        get() = File(System.getProperty("java.io.tmpdir"))

    private fun TestScope.createRepository(): KrateClaimsRepository {
        return KrateClaimsRepository(
            folder = tempFile.resolve(UUID.randomUUID().toString()),
            stringFormat = YamlStringFormat(),
            ioScope = backgroundScope
        )
    }

    @Test
    fun GIVEN_free_chunk_WHEN_claimed_and_deleted_THEN_chunk_count_follows(): TestResult {
        return runTest {
            val repository = createRepository()
            randomChunk.let { chunk ->
                assertEquals(0, repository.loadAllChunks().size)
                repository.claim(claimPlayer.uuid, chunk)
                assertEquals(1, repository.loadAllChunks().size)
                awaitForCompletion { chunk.uniqueWorldKey in repository.chunkByKrate }
                repository.deleteChunk(claimPlayer.uuid, chunk.uniqueWorldKey)
                assertEquals(0, repository.loadAllChunks().size)
            }
        }
    }

    @Test
    fun GIVEN_claimed_chunk_WHEN_flag_is_set_THEN_chunk_keeps_the_flag_value(): TestResult {
        return runTest {
            val repository = createRepository()
            randomChunk.let { chunk ->
                repository.claim(claimPlayer.uuid, chunk)
                repository.setFlag(
                    uuid = claimPlayer.uuid,
                    flag = ChunkFlag.ALLOW_BREAK,
                    value = true,
                    key = chunk.uniqueWorldKey
                )
                repository.loadAllChunks().first().flags[ChunkFlag.ALLOW_BREAK].let { flagValue ->
                    assertNotNull(flagValue)
                    assertTrue(flagValue)
                }
                repository.setFlag(
                    uuid = claimPlayer.uuid,
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

    @Test
    fun GIVEN_chunk_WHEN_claimed_and_flag_changes_THEN_action_permission_follows(): TestResult {
        return runTest {
            val repository = createRepository()
            randomChunk.let { chunk ->
                assertTrue { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
                repository.claim(claimPlayer.uuid, chunk)
                assertFalse { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
                repository.setFlag(
                    uuid = claimPlayer.uuid,
                    flag = ChunkFlag.ALLOW_BREAK,
                    value = true,
                    key = chunk.uniqueWorldKey
                )
                assertTrue { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
                repository.setFlag(
                    uuid = claimPlayer.uuid,
                    flag = ChunkFlag.ALLOW_BREAK,
                    value = false,
                    key = chunk.uniqueWorldKey
                )
                assertFalse { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
            }
        }
    }

    @Test
    fun GIVEN_claimed_chunk_WHEN_mapped_with_size_three_THEN_only_center_is_claimed(): TestResult {
        return runTest {
            val repository = createRepository()
            randomChunk.let { chunk ->
                repository.claim(claimPlayer.uuid, chunk)
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
                assertContentEquals(expectArray, repository.createGrid(3, chunk).map { row -> row.toList() }.toList())
            }
        }
    }
}
