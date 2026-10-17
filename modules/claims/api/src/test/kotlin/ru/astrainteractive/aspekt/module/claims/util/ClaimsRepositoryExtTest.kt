@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.claims.util

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

    private fun TestScope.getRepository(): KrateClaimsRepository {
        return KrateClaimsRepository(
            folder = tempFile.resolve(UUID.randomUUID().toString()),
            stringFormat = YamlStringFormat(),
            ioScope = backgroundScope
        )
    }

    @Test
    fun GIVEN_free_chunk_WHEN_claimed_and_deleted_THEN_chunk_count_follows(): Unit = runTest {
        val repository = getRepository()
        randomChunk.let { chunk ->
            assertEquals(0, repository.getAllChunks().size)
            repository.claim(claimPlayer.uuid, chunk)
            assertEquals(1, repository.getAllChunks().size)
            awaitForCompletion { chunk.uniqueWorldKey in repository.chunkByKrate }
            repository.deleteChunk(claimPlayer.uuid, chunk.uniqueWorldKey)
            assertEquals(0, repository.getAllChunks().size)
        }
    }

    @Test
    fun GIVEN_claimed_chunk_WHEN_flag_is_set_THEN_chunk_keeps_the_flag_value(): Unit = runTest {
        val repository = getRepository()
        randomChunk.let { chunk ->
            repository.claim(claimPlayer.uuid, chunk)
            repository.setFlag(claimPlayer.uuid, ChunkFlag.ALLOW_BREAK, true, chunk.uniqueWorldKey)
            repository.getAllChunks().first().flags[ChunkFlag.ALLOW_BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertTrue(flagValue)
            }
            repository.setFlag(claimPlayer.uuid, ChunkFlag.ALLOW_BREAK, false, chunk.uniqueWorldKey)
            repository.getAllChunks().first().flags[ChunkFlag.ALLOW_BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertFalse(flagValue)
            }
        }
    }

    @Test
    fun GIVEN_chunk_WHEN_claimed_and_flag_changes_THEN_action_permission_follows(): Unit = runTest {
        val repository = getRepository()
        randomChunk.let { chunk ->
            assertTrue { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
            repository.claim(claimPlayer.uuid, chunk)
            assertFalse { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
            repository.setFlag(claimPlayer.uuid, ChunkFlag.ALLOW_BREAK, true, chunk.uniqueWorldKey)
            assertTrue { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
            repository.setFlag(claimPlayer.uuid, ChunkFlag.ALLOW_BREAK, false, chunk.uniqueWorldKey)
            assertFalse { repository.isAble(chunk.uniqueWorldKey, ChunkFlag.ALLOW_BREAK) }
        }
    }

    @Test
    fun GIVEN_claimed_chunk_WHEN_mapped_with_size_three_THEN_only_center_is_claimed(): Unit = runTest {
        val repository = getRepository()
        randomChunk.let { chunk ->
            repository.claim(claimPlayer.uuid, chunk)
            val expectArray = listOf(
                listOf(false, false, false),
                listOf(false, true, false),
                listOf(false, false, false)
            )
            assertContentEquals(expectArray, repository.map(3, chunk).map { row -> row.toList() }.toList())
        }
    }
}
