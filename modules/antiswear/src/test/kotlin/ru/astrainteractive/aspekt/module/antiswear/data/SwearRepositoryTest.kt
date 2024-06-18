package ru.astrainteractive.aspekt.module.antiswear.data

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.bukkit.entity.Player
import org.mockito.Mockito
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.io.File
import java.nio.file.Files
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SwearRepositoryTest {
    private val tempFolder: File
        get() = Files.createTempDirectory("swear_test").toFile()

    @AfterTest
    fun cleanup() {
        tempFolder.delete()
    }

    @Test
    fun test(): Unit = runTest {
        val repository = SwearRepositoryImpl(
            dispatchers = DefaultKotlinDispatchers,
            tempFileStringFormat = Json,
            folder = tempFolder
        )
        val player = Mockito.mock(Player::class.java)
        val uuid = UUID.randomUUID()
        Mockito.`when`(player.name).then { "Name" }
        Mockito.`when`(player.uniqueId).then { uuid }
        assertTrue(repository.isSwearFilterEnabled(player))
        repository.setSwearFilterEnabled(player, false)
        assertFalse(repository.isSwearFilterEnabled(player))
        repository.setSwearFilterEnabled(player, true)
        assertTrue(repository.isSwearFilterEnabled(player))
        repository.forgetPlayer(player)
        repository.rememberPlayer(player)
        assertTrue(repository.isSwearFilterEnabled(player))
        repository.setSwearFilterEnabled(player, false)
        assertFalse(repository.isSwearFilterEnabled(player))
        repository.forgetPlayer(player)
        repository.rememberPlayer(player)
        assertFalse(repository.isSwearFilterEnabled(player))
    }
}
