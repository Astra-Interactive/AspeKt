package ru.astrainteractive.aspekt.adminprivate.controller

import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.astrainteractive.aspekt.module.adminprivate.controller.AdminPrivateController
import ru.astrainteractive.aspekt.module.adminprivate.controller.di.AdminPrivateControllerDependencies
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepository
import ru.astrainteractive.aspekt.module.adminprivate.data.AdminPrivateRepositoryImpl
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminChunk
import ru.astrainteractive.aspekt.module.adminprivate.model.ChunkFlag
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kdi.Provider
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.io.File
import java.util.UUID
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class AdminPrivateControllerTest {
    private val randomChunk: AdminChunk by Provider {
        val x = Random.nextInt(0, 100)
        val z = Random.nextInt(0, 100)
        AdminChunk(
            x = x,
            z = z,
            worldName = UUID.randomUUID().toString(),
            flags = emptyMap(),
            chunkKey = "$x$z".toLong()
        )
    }
    private val tempFile by Provider {
        File(System.getProperty("java.io.tmpdir"))
    }

    inner class Dependencies : AdminPrivateControllerDependencies {
        override val repository: AdminPrivateRepository =
            AdminPrivateRepositoryImpl(
                file = tempFile.resolve(UUID.randomUUID().toString()),
                dispatchers = DefaultKotlinDispatchers,
                stringFormat = YamlStringFormat()
            )
    }

    @Test
    fun testClaimAndUnclaim(): Unit = runBlocking {
        val module = Dependencies()
        val controller = AdminPrivateController(module)
        randomChunk.let { chunk ->
            controller.claim(chunk)
            assertEquals(1, module.repository.getAllChunks().size)
            controller.unclaim(chunk)
            assertEquals(0, module.repository.getAllChunks().size)
        }
    }

    @Test
    fun testSetFlag(): Unit = runBlocking {
        val module = Dependencies()
        val controller = AdminPrivateController(module)
        randomChunk.let { chunk ->
            controller.claim(chunk)
            controller.setFlag(ChunkFlag.BREAK, true, chunk)
            module.repository.getAllChunks().first().flags[ChunkFlag.BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertTrue(flagValue)
            }
            controller.setFlag(ChunkFlag.BREAK, false, chunk)
            module.repository.getAllChunks().first().flags[ChunkFlag.BREAK].let { flagValue ->
                assertNotNull(flagValue)
                assertFalse(flagValue)
            }
        }
    }

    @Test
    fun testIsAble(): Unit = runBlocking {
        val module = Dependencies()
        val controller = AdminPrivateController(module)
        randomChunk.let { chunk ->
            assertTrue { controller.isAble(chunk, ChunkFlag.BREAK) }
            controller.claim(chunk)
            assertFalse { controller.isAble(chunk, ChunkFlag.BREAK) }
            controller.setFlag(ChunkFlag.BREAK, true, chunk)
            assertTrue { controller.isAble(chunk, ChunkFlag.BREAK) }
            controller.setFlag(ChunkFlag.BREAK, false, chunk)
            assertFalse { controller.isAble(chunk, ChunkFlag.BREAK) }
        }
    }

    @Test
    fun testMapThree(): Unit = runBlocking {
        val module = Dependencies()
        val controller = AdminPrivateController(module)
        randomChunk.let { chunk ->
            controller.claim(chunk)
            val expectArray = listOf(
                listOf(false, false, false),
                listOf(false, true, false),
                listOf(false, false, false)
            )
            assertContentEquals(expectArray, controller.map(3, chunk).map { it.toList() }.toList())
        }
    }
}
