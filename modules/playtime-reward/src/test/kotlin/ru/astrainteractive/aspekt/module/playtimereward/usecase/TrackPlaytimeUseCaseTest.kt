@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.playtimereward.usecase

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import ru.astrainteractive.aspekt.module.playtimereward.fake.FakeClock
import ru.astrainteractive.aspekt.module.playtimereward.fake.FakeEconomyFacade
import ru.astrainteractive.aspekt.module.playtimereward.fake.FakeOnlineKPlayer
import ru.astrainteractive.aspekt.module.playtimereward.krate.KratePlaytimeRewardRepository
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeEvent
import ru.astrainteractive.aspekt.module.playtimereward.model.PlaytimeRewardConfiguration
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.klibs.kstorage.api.asCachedKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.nio.file.Files
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class TrackPlaytimeUseCaseTest {
    private val folder = Files.createTempDirectory("playtime_test").toFile()
    private val clock = FakeClock(Instant.fromEpochSeconds(0))
    private val economy = FakeEconomyFacade()
    private val player = FakeOnlineKPlayer(uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), name = "Player")
    private val configuration = PlaytimeRewardConfiguration(rewardAmount = 5.0, requiredDuration = 60.minutes)
    private val repository = KratePlaytimeRewardRepository(tempDataFolder = folder, stringFormat = Json, clock = clock)
    private val useCase = TrackPlaytimeUseCase(
        clock = clock,
        playtimeRewardRepository = repository,
        economyFacade = economy,
        playtimeRewardConfigurationKrate = DefaultMutableKrate(factory = { configuration }, loader = { null }).asCachedKrate(),
        pluginTranslationKrate = DefaultMutableKrate(factory = ::PluginTranslation, loader = { null }).asCachedKrate()
    )

    @AfterTest
    fun cleanup() {
        folder.deleteRecursively()
    }

    @Test
    fun GIVEN_player_played_long_enough_WHEN_checked_THEN_player_is_rewarded_and_playtime_resets() = runTest {
        useCase.invoke(player, PlaytimeEvent.Joined)
        clock.current = clock.current + 61.minutes

        useCase.invoke(player, PlaytimeEvent.Checked)

        assertEquals(5.0, economy.deposits[player.uuid])
        assertEquals(1, player.messages.size)
        assertEquals(0.minutes, repository.krateOf(player).getValue().timePlayed)
    }

    @Test
    fun GIVEN_player_played_too_little_WHEN_checked_THEN_playtime_is_counted_without_reward() = runTest {
        useCase.invoke(player, PlaytimeEvent.Joined)
        clock.current = clock.current + 30.minutes

        useCase.invoke(player, PlaytimeEvent.Checked)

        assertTrue(economy.deposits.isEmpty())
        assertTrue(player.messages.isEmpty())
        assertEquals(30.minutes, repository.krateOf(player).getValue().timePlayed)
    }

    @Test
    fun GIVEN_player_left_and_rejoined_WHEN_checked_THEN_time_offline_is_not_counted() = runTest {
        useCase.invoke(player, PlaytimeEvent.Joined)
        clock.current = clock.current + 40.minutes
        useCase.invoke(player, PlaytimeEvent.Left)
        clock.current = clock.current + 120.minutes
        useCase.invoke(player, PlaytimeEvent.Joined)
        clock.current = clock.current + 10.minutes

        useCase.invoke(player, PlaytimeEvent.Checked)

        assertTrue(economy.deposits.isEmpty())
        assertEquals(50.minutes, repository.krateOf(player).getValue().timePlayed)
    }
}
