package ru.astrainteractive.aspekt.claims.controller

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.astrainteractive.aspekt.module.claims.controller.ClaimController
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepositoryImpl
import ru.astrainteractive.aspekt.module.claims.data.getAllChunks
import ru.astrainteractive.aspekt.module.claims.model.ChunkFlag
import ru.astrainteractive.aspekt.module.claims.model.ClaimChunk
import ru.astrainteractive.aspekt.module.claims.model.ClaimPlayer
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
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

    private fun getRepository(): ClaimsRepositoryImpl {
        return ClaimsRepositoryImpl(
            folder = tempFile.resolve(UUID.randomUUID().toString()),
            stringFormat = YamlStringFormat(),
            scope = GlobalScope
        )
    }

    @Test
    fun testClaimAndUnclaim(): Unit = runBlocking {
        val repository = getRepository()
        val controller = ClaimController(repository)
        randomChunk.let { chunk ->
            controller.claim(claimPlayer, chunk)
            assertEquals(1, repository.getAllChunks().size)
            controller.unclaim(claimPlayer, chunk)
            assertEquals(0, repository.getAllChunks().size)
        }
    }

    @Test
    fun testSetFlag(): Unit = runBlocking {
        val repository = getRepository()
        val controller = ClaimController(repository)
        randomChunk.let { chunk ->
            controller.claim(claimPlayer, chunk)
            controller.setFlag(claimPlayer, ChunkFlag.ALLOW_BREAK, true, chunk)
            repository.getAllChunks().first().flags[ChunkFlag.ALLOW_BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertTrue(flagValue)
            }
            controller.setFlag(claimPlayer, ChunkFlag.ALLOW_BREAK, false, chunk)
            repository.getAllChunks().first().flags[ChunkFlag.ALLOW_BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertFalse(flagValue)
            }
        }
    }

    @Test
    fun testIsAble(): Unit = runBlocking {
        val repository = getRepository()
        val controller = ClaimController(repository)
        randomChunk.let { chunk ->
            assertTrue { controller.isAble(chunk, ChunkFlag.ALLOW_BREAK) }
            controller.claim(claimPlayer, chunk)
            assertFalse { controller.isAble(chunk, ChunkFlag.ALLOW_BREAK) }
            controller.setFlag(claimPlayer, ChunkFlag.ALLOW_BREAK, true, chunk)
            assertTrue { controller.isAble(chunk, ChunkFlag.ALLOW_BREAK) }
            controller.setFlag(claimPlayer, ChunkFlag.ALLOW_BREAK, false, chunk)
            assertFalse { controller.isAble(chunk, ChunkFlag.ALLOW_BREAK) }
        }
    }

    @Test
    fun testMapThree(): Unit = runBlocking {
        val repository = getRepository()
        val controller = ClaimController(repository)
        randomChunk.let { chunk ->
            controller.claim(claimPlayer, chunk)
            val expectArray = listOf(
                listOf(false, false, false),
                listOf(false, true, false),
                listOf(false, false, false)
            )
            assertContentEquals(expectArray, controller.map(3, chunk).map { it.toList() }.toList())
        }
    }
}
