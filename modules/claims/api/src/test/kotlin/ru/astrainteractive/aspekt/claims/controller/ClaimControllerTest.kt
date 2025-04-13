package ru.astrainteractive.aspekt.claims.controller

import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.astrainteractive.aspekt.module.claims.controller.ClaimController
import ru.astrainteractive.aspekt.module.claims.controller.di.ClaimControllerDependencies
import ru.astrainteractive.aspekt.module.claims.data.ClaimsRepository
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

    inner class Dependencies : ClaimControllerDependencies {
        override val repository: ClaimsRepository =
            ClaimsRepositoryImpl(
                folder = tempFile.resolve(UUID.randomUUID().toString()),
                stringFormat = YamlStringFormat()
            )
    }

    @Test
    fun testClaimAndUnclaim(): Unit = runBlocking {
        val module = Dependencies()
        val controller = ClaimController(module)
        randomChunk.let { chunk ->
            controller.claim(claimPlayer, chunk)
            assertEquals(1, module.repository.getAllChunks().size)
            controller.unclaim(claimPlayer, chunk)
            assertEquals(0, module.repository.getAllChunks().size)
        }
    }

    @Test
    fun testSetFlag(): Unit = runBlocking {
        val module = Dependencies()
        val controller = ClaimController(module)
        randomChunk.let { chunk ->
            controller.claim(claimPlayer, chunk)
            controller.setFlag(claimPlayer, ChunkFlag.BREAK, true, chunk)
            module.repository.getAllChunks().first().flags[ChunkFlag.BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertTrue(flagValue)
            }
            controller.setFlag(claimPlayer, ChunkFlag.BREAK, false, chunk)
            module.repository.getAllChunks().first().flags[ChunkFlag.BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertFalse(flagValue)
            }
        }
    }

    @Test
    fun testIsAble(): Unit = runBlocking {
        val module = Dependencies()
        val controller = ClaimController(module)
        randomChunk.let { chunk ->
            assertTrue { controller.isAble(chunk, ChunkFlag.BREAK) }
            controller.claim(claimPlayer, chunk)
            assertFalse { controller.isAble(chunk, ChunkFlag.BREAK) }
            controller.setFlag(claimPlayer, ChunkFlag.BREAK, true, chunk)
            assertTrue { controller.isAble(chunk, ChunkFlag.BREAK) }
            controller.setFlag(claimPlayer, ChunkFlag.BREAK, false, chunk)
            assertFalse { controller.isAble(chunk, ChunkFlag.BREAK) }
        }
    }

    @Test
    fun testMapThree(): Unit = runBlocking {
        val module = Dependencies()
        val controller = ClaimController(module)
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
