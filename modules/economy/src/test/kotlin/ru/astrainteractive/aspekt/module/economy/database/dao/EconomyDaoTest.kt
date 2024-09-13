package ru.astrainteractive.aspekt.module.economy.database.dao

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.aspekt.module.economy.model.PlayerModel
import ru.astrainteractive.astralibs.exposed.model.DatabaseConfiguration
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultStateFlowMutableKrate
import ru.astrainteractive.klibs.kstorage.util.KrateExt.update
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EconomyDaoTest {
    private val scheduler = TestCoroutineScheduler()
    private var _folder: File? = null
    private val requireFolder: File
        get() = _folder ?: error("Folder not set")

    private var _module: EconomyDatabaseModule? = null
    private val requireModule: EconomyDatabaseModule
        get() = _module ?: error("Module not set")

    private val dbConfig = DefaultStateFlowMutableKrate<DatabaseConfiguration>(
        factory = { DatabaseConfiguration.H2("test") },
        loader = { DatabaseConfiguration.H2("test") }
    )

    @BeforeTest
    fun setup() {
        _folder = File("temp-folder")
        requireFolder.mkdirs()
        _module = EconomyDatabaseModule.Default(
            dataFolder = requireFolder,
            dbConfig = dbConfig,
            coroutineScope = CoroutineScope(scheduler),
            ioDispatcher = scheduler
        )
    }

    @AfterTest
    fun tearDown() {
        requireFolder.deleteRecursively()
    }

    @Test
    fun `GIVEN_different_db_configs_WHEN_try_THEN_db_changed`() = runTest {
        val initialUrl = requireModule.databaseFlow.first().url
        dbConfig.update { DatabaseConfiguration.H2("test1") }
        requireModule.databaseFlow
            .filter { it.url != initialUrl }
            .first()
        val test1Url = requireModule.databaseFlow.first().url

        val currencies = listOf(CurrencyModel(id = "0", name = "name", priority = 0))
        requireModule.economyDao.updateCurrencies(currencies)
        assertEquals(1, requireModule.economyDao.getAllCurrencies().size)

        dbConfig.update { DatabaseConfiguration.H2("test2") }
        requireModule.databaseFlow
            .filter { it.url != test1Url }
            .first()

        assertEquals(0, requireModule.economyDao.getAllCurrencies().size)
        dbConfig.update { DatabaseConfiguration.H2("test1") }
        requireModule.databaseFlow
            .filter { it.url == test1Url }
            .first()
        assertEquals(1, requireModule.economyDao.getAllCurrencies().size)
    }

    @Test
    fun `GIVEN_currency_WHEN_try_find_THEN_found`() = runTest(scheduler) {
        val currencies = listOf(CurrencyModel(id = "0", name = "name", priority = 0))
        requireModule.economyDao.updateCurrencies(currencies)
        assertEquals(1, requireModule.economyDao.getAllCurrencies().size)
        assertEquals(currencies.first(), requireModule.economyDao.findCurrency(currencies.first().id))
    }

    @Test
    fun `GIVEN_multiple_currencies_WHEN_change_list_THEN_changed`() = runTest(scheduler) {
        val currencies = listOf(CurrencyModel(id = "0", name = "name", priority = 0))
        requireModule.economyDao.updateCurrencies(currencies)
        val newCurrencies = List(2) { CurrencyModel(id = "$it", name = "name$it", priority = 0) }
        requireModule.economyDao.updateCurrencies(newCurrencies)
        assertContentEquals(newCurrencies, requireModule.economyDao.getAllCurrencies())
        newCurrencies.forEach {
            assertEquals(it, requireModule.economyDao.findCurrency(it.id))
        }
    }

    @Test
    fun `GIVEN_currency_WHEN_add_to_player_THEN_added`() = runTest(scheduler) {
        val currencies = List(2) { CurrencyModel(id = "$it", name = "name$it", priority = 0) }
        requireModule.economyDao.updateCurrencies(currencies)
        val playerCurrency = PlayerCurrency(
            playerModel = PlayerModel(
                name = "player1",
                uuid = "uuid1",
            ),
            balance = 0.0,
            currencyModel = currencies.first()
        )
        assertEquals(0, requireModule.economyDao.playerCurrencies(playerCurrency.playerModel.uuid).size)
        requireModule.economyDao.updatePlayerCurrency(playerCurrency)
        assertEquals(
            playerCurrency,
            requireModule.economyDao.findPlayerCurrency(
                playerCurrency.playerModel.uuid,
                playerCurrency.currencyModel.id
            )
        )
        assertEquals(1, requireModule.economyDao.playerCurrencies(playerCurrency.playerModel.uuid).size)
        assertEquals(1, requireModule.economyDao.topCurrency(playerCurrency.currencyModel.id, 0, 10).size)
    }

    @Test
    fun `GIVEN_currency_WHEN_transfer_to_player_THEN_transfered`() = runTest(scheduler) {
        val currencies = List(2) { CurrencyModel(id = "$it", name = "name$it", priority = 0) }
        requireModule.economyDao.updateCurrencies(currencies)
        val playerCurrency = PlayerCurrency(
            playerModel = PlayerModel(
                name = "player1",
                uuid = "uuid1",
            ),
            balance = 100.0,
            currencyModel = currencies.first()
        )
        val otherPlayerModel = PlayerModel(
            name = "player2",
            uuid = "uuid2",
        )
        requireModule.economyDao.updatePlayerCurrency(playerCurrency)
        assertTrue(
            requireModule.economyDao.transfer(
                from = playerCurrency.playerModel,
                to = otherPlayerModel,
                amount = playerCurrency.balance,
                currencyId = playerCurrency.currencyModel.id
            )
        )
        assertEquals(
            0.0,
            requireModule.economyDao.findPlayerCurrency(
                playerCurrency.playerModel.uuid,
                currencies.first().id
            )?.balance
        )
        assertNull(
            requireModule.economyDao.findPlayerCurrency(
                playerCurrency.playerModel.uuid,
                currencies.last().id
            )
        )

        assertEquals(
            100.0,
            requireModule.economyDao.findPlayerCurrency(
                otherPlayerModel.uuid,
                currencies.first().id
            )?.balance
        )
        assertNull(
            requireModule.economyDao.findPlayerCurrency(
                otherPlayerModel.uuid,
                currencies.last().id
            )
        )
    }
}
