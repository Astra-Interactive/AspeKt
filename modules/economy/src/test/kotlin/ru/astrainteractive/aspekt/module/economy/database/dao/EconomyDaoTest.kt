package ru.astrainteractive.aspekt.module.economy.database.dao

import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.aspekt.module.economy.database.di.EconomyDatabaseModule
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.module.economy.model.DatabaseConfiguration
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.aspekt.module.economy.model.PlayerModel
import java.io.File
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EconomyDaoTest {
    private val scheduler = TestCoroutineScheduler()
    private var _module: EconomyDatabaseModule? = null
    private var _folder: File? = null
    private val requireFolder: File
        get() = _folder ?: error("Folder not set")
    private val requireModule: EconomyDatabaseModule
        get() = _module ?: error("Module not set")

    @BeforeTest
    fun setup() {
        _folder = File("./temp${Random.nextInt()}")
        requireFolder.mkdirs()
        requireFolder.deleteOnExit()
        _module = EconomyDatabaseModule.Default(
            dataFolder = requireFolder,
            dbConfig = DatabaseConfiguration.H2
        )
    }

    @AfterTest
    fun tearDown() {
        requireFolder.deleteRecursively()
    }

    @Test
    fun `GIVEN_currency_WHEN_try_find_THEN_found`() = runTest(scheduler) {
        val currencies = listOf(CurrencyModel(id = "0", name = "name", isPrimary = false))
        requireModule.economyDao.updateCurrencies(currencies)
        assertEquals(1, requireModule.economyDao.getAllCurrencies().size)
        assertEquals(currencies.first(), requireModule.economyDao.findCurrency(currencies.first().id))
    }

    @Test
    fun `GIVEN_multiple_currencies_WHEN_change_list_THEN_changed`() = runTest(scheduler) {
        val currencies = listOf(CurrencyModel(id = "0", name = "name", isPrimary = false))
        requireModule.economyDao.updateCurrencies(currencies)
        val newCurrencies = List(2) { CurrencyModel(id = "$it", name = "name$it", isPrimary = false) }
        requireModule.economyDao.updateCurrencies(newCurrencies)
        assertContentEquals(newCurrencies, requireModule.economyDao.getAllCurrencies())
        newCurrencies.forEach {
            assertEquals(it, requireModule.economyDao.findCurrency(it.id))
        }
    }

    @Test
    fun `GIVEN_currency_WHEN_add_to_player_THEN_added`() = runTest(scheduler) {
        val currencies = List(2) { CurrencyModel(id = "$it", name = "name$it", isPrimary = false) }
        requireModule.economyDao.updateCurrencies(currencies)
        val playerCurrency = PlayerCurrency(
            playerModel = PlayerModel(
                name = "player1",
                uuid = "uuid1",
            ),
            amount = 0.0,
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
        val currencies = List(2) { CurrencyModel(id = "$it", name = "name$it", isPrimary = false) }
        requireModule.economyDao.updateCurrencies(currencies)
        val playerCurrency = PlayerCurrency(
            playerModel = PlayerModel(
                name = "player1",
                uuid = "uuid1",
            ),
            amount = 100.0,
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
                amount = playerCurrency.amount,
                currencyId = playerCurrency.currencyModel.id
            )
        )
        assertEquals(
            0.0,
            requireModule.economyDao.findPlayerCurrency(
                playerCurrency.playerModel.uuid,
                currencies.first().id
            )?.amount
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
            )?.amount
        )
        assertNull(
            requireModule.economyDao.findPlayerCurrency(
                otherPlayerModel.uuid,
                currencies.last().id
            )
        )
    }
}
