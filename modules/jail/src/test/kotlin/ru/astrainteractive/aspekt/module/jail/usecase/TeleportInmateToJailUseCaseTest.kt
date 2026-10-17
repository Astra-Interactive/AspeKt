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
class TeleportInmateToJailUseCaseTest {
    private val player = FakeOnlineKPlayer(uuid = JailTestData.inmateUuid, name = "Prisoner")

    private fun useCase(repository: FakeJailRepository, onlinePlayers: List<FakeOnlineKPlayer>) =
        TeleportInmateToJailUseCase(
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
    fun GIVEN_online_inmate_WHEN_teleported_to_jail_THEN_player_is_in_the_jail() = runTest {
        val repository = FakeJailRepository(jails = listOf(JailTestData.jail), inmates = listOf(JailTestData.inmate))

        useCase(repository, onlinePlayers = listOf(player)).invoke(JailTestData.inmate)

        assertEquals(listOf(KLocation(x = 1.0, y = 2.0, z = 3.0, worldName = "world")), player.teleports)
    }

    @Test
    fun GIVEN_missing_jail_WHEN_teleported_to_jail_THEN_player_stays() = runTest {
        val repository = FakeJailRepository(jails = emptyList(), inmates = listOf(JailTestData.inmate))

        useCase(repository, onlinePlayers = listOf(player)).invoke(JailTestData.inmate)

        assertTrue(player.teleports.isEmpty())
    }

    @Test
    fun GIVEN_offline_inmate_WHEN_teleported_to_jail_THEN_nothing_happens() = runTest {
        val repository = FakeJailRepository(jails = listOf(JailTestData.jail), inmates = listOf(JailTestData.inmate))

        useCase(repository, onlinePlayers = emptyList()).invoke(JailTestData.inmate)

        assertTrue(player.teleports.isEmpty())
    }
}
