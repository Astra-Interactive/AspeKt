package ru.astrainteractive.aspekt.claims.controller

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepositoryImpl
import ru.astrainteractive.aspekt.module.claims.data.claim
import ru.astrainteractive.aspekt.module.claims.data.getAllChunks
import ru.astrainteractive.aspekt.module.claims.data.isAble
import ru.astrainteractive.aspekt.module.claims.data.map
import ru.astrainteractive.aspekt.module.claims.data.setFlag
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.aspekt.module.claims.util.uniqueWorldKey
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

internal class ClaimControllerTest {
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

    private fun TestScope.getRepository(): ClaimsRepositoryImpl {
        return ClaimsRepositoryImpl(
            folder = tempFile.resolve(UUID.randomUUID().toString()),
            stringFormat = YamlStringFormat(),
            scope = backgroundScope
        )
    }

    @Test
    fun testClaimAndUnclaim(): Unit = runTest {
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
    fun testSetFlag(): Unit = runTest {
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
    fun testIsAble(): Unit = runTest {
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
    fun testMapThree(): Unit = runTest {
        val repository = getRepository()
        randomChunk.let { chunk ->
            repository.claim(claimPlayer.uuid, chunk)
            val expectArray = listOf(
                listOf(false, false, false),
                listOf(false, true, false),
                listOf(false, false, false)
            )
            assertContentEquals(expectArray, repository.map(3, chunk).map { it.toList() }.toList())
        }
    }
}
