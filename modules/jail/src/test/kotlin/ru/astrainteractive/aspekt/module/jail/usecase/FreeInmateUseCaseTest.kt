@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.jail.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ru.astrainteractive.aspekt.module.jail.fake.FakeJailRepository
import ru.astrainteractive.aspekt.module.jail.fake.FakeOnlineKPlayer
import ru.astrainteractive.aspekt.module.jail.fake.FakePlatformServer
import ru.astrainteractive.astralibs.server.location.KLocation
import ru.astrainteractive.klibs.mikro.core.dispatchers.DefaultKotlinDispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FreeInmateUseCaseTest {
    private val repository = FakeJailRepository(
        jails = listOf(JailTestData.jail),
        inmates = listOf(JailTestData.inmate)
    )
    private val player = FakeOnlineKPlayer(uuid = JailTestData.inmateUuid, name = "Prisoner")

    private fun useCase(onlinePlayers: List<FakeOnlineKPlayer>) = FreeInmateUseCase(
        jailRepository = repository,
        platformServer = FakePlatformServer(onlinePlayers),
        dispatchers = DefaultKotlinDispatchers
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun GIVEN_online_inmate_WHEN_freed_THEN_inmate_is_removed_and_returns_to_last_location() = runTest {
        val result = useCase(onlinePlayers = listOf(player)).invoke(JailTestData.inmate)

        assertTrue(result.isSuccess)
        assertTrue(repository.currentInmates.isEmpty())
        assertEquals(listOf(KLocation(x = 10.0, y = 20.0, z = 30.0, worldName = "world")), player.teleports)
    }

    @Test
    fun GIVEN_offline_inmate_WHEN_freed_THEN_inmate_is_removed_without_teleport() = runTest {
        val result = useCase(onlinePlayers = emptyList()).invoke(JailTestData.inmate)

        assertTrue(result.isSuccess)
        assertTrue(repository.currentInmates.isEmpty())
        assertTrue(player.teleports.isEmpty())
    }

    @Test
    fun GIVEN_storage_fails_WHEN_freed_THEN_returns_failure_and_player_stays() = runTest {
        repository.isFreeFailing = true

        val result = useCase(onlinePlayers = listOf(player)).invoke(JailTestData.inmate)

        assertTrue(result.isFailure)
        assertEquals(listOf(JailTestData.inmate), repository.currentInmates)
        assertTrue(player.teleports.isEmpty())
    }
}
