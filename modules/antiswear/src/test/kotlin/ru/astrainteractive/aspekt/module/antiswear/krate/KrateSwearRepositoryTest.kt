@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.antiswear.krate

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.mockito.Mockito
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import java.io.File
import java.nio.file.Files
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KrateSwearRepositoryTest {
    private val tempFolder: File
        get() = Files.createTempDirectory("swear_test").toFile()

    @AfterTest
    fun cleanup() {
        tempFolder.delete()
    }

    private fun repository(): KrateSwearRepository {
        return KrateSwearRepository(
            dispatchers = DefaultKotlinDispatchers,
            tempFileStringFormat = Json,
            folder = tempFolder
        )
    }

    private fun player(): OnlineKPlayer {
        val player = Mockito.mock(OnlineKPlayer::class.java)
        val uuid = UUID.randomUUID()
        Mockito.`when`(player.name).then { "Name" }
        Mockito.`when`(player.uuid).then { uuid }
        return player
    }

    @Test
    fun GIVEN_unknown_player_WHEN_checked_THEN_filter_is_enabled() {
        runTest {
            assertTrue(repository().isSwearFilterEnabled(player()))
        }
    }

    @Test
    fun GIVEN_player_WHEN_filter_is_toggled_THEN_check_follows_the_toggle() {
        runTest {
            val repository = repository()
            val player = player()

            repository.setSwearFilterEnabled(player, false)
            assertFalse(repository.isSwearFilterEnabled(player))
            repository.setSwearFilterEnabled(player, true)
            assertTrue(repository.isSwearFilterEnabled(player))
        }
    }

    @Test
    fun GIVEN_disabled_filter_WHEN_player_rejoins_THEN_filter_stays_disabled() {
        runTest {
            val repository = repository()
            val player = player()
            repository.setSwearFilterEnabled(player, false)

            repository.forgetPlayer(player)
            repository.loadPlayer(player)

            assertFalse(repository.isSwearFilterEnabled(player))
        }
    }

    @Test
    fun GIVEN_enabled_filter_WHEN_player_rejoins_THEN_filter_stays_enabled() {
        runTest {
            val repository = repository()
            val player = player()
            repository.setSwearFilterEnabled(player, true)

            repository.forgetPlayer(player)
            repository.loadPlayer(player)

            assertTrue(repository.isSwearFilterEnabled(player))
        }
    }
}
