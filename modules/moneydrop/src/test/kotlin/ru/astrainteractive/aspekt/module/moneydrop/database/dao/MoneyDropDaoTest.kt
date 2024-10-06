package ru.astrainteractive.aspekt.module.moneydrop.database.dao

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.aspekt.module.moneydrop.database.di.MoneyDropDaoModule
import ru.astrainteractive.aspekt.module.moneydrop.database.model.MoneyDropLocation
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MoneyDropDaoTest {
    private val scheduler = TestCoroutineScheduler()
    private var _module: MoneyDropDaoModule? = null
    private val requireModule: MoneyDropDaoModule
        get() = _module ?: error("Module not set")
    private val folder = File("./temp")

    @BeforeTest
    fun setup() {
        folder.mkdirs()
        folder.deleteOnExit()
        _module = MoneyDropDaoModule.Default(
            dataFolder = folder,
            ioDispatcher = scheduler,
            coroutineScope = CoroutineScope(scheduler),
        )
    }

    @AfterTest
    fun tearDown() {
        folder.deleteRecursively()
    }

    @Test
    fun testAll() = runTest(scheduler) {
        val location = MoneyDropLocation(
            x = 0,
            y = 0,
            z = 0,
            world = "world"
        )
        assertFalse(requireModule.dao.isLocationExists(location))
        requireModule.dao.addLocation(location)
        assertTrue(requireModule.dao.isLocationExists(location))
    }
}
