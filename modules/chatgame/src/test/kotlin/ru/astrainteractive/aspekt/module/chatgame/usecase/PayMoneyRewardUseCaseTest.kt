@file:Suppress("FunctionNaming")

package ru.astrainteractive.aspekt.module.chatgame.usecase

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import ru.astrainteractive.aspekt.economy.api.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.chatgame.fake.FakeEconomyFacade
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.astralibs.economy.EconomyFacade
import java.util.UUID
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PayMoneyRewardUseCaseTest {
    private val winnerUuid = UUID.fromString("0f8fad5b-d9cb-469f-a165-70867728950e")
    private val fixedReward = Reward.Money(minAmount = 25.0, maxAmount = 25.0)

    private fun payer(
        defaultEconomy: FakeEconomyFacade?,
        economiesByCurrencyId: Map<String, FakeEconomyFacade> = emptyMap()
    ): PayMoneyRewardUseCase {
        val provider = object : CurrencyEconomyProviderFactory {
            override fun findByCurrencyId(currencyId: String): EconomyFacade? {
                return economiesByCurrencyId[currencyId]
            }

            override fun findDefault(): EconomyFacade? = defaultEconomy
        }
        return PayMoneyRewardUseCase(
            currencyEconomyProviderFactory = provider,
            random = Random(seed = 42)
        )
    }

    @Test
    fun GIVEN_reward_without_currency_WHEN_paid_THEN_default_economy_receives_the_amount(): TestResult {
        return runTest {
            val defaultEconomy = FakeEconomyFacade(acceptsDeposits = true)

            val paid = payer(defaultEconomy).invoke(winnerUuid, fixedReward)

            assertEquals(25, paid)
            assertEquals(mapOf(winnerUuid to 25.0), defaultEconomy.deposits)
        }
    }

    @Test
    fun GIVEN_reward_in_a_currency_WHEN_paid_THEN_only_that_currency_receives_the_amount(): TestResult {
        return runTest {
            val defaultEconomy = FakeEconomyFacade(acceptsDeposits = true)
            val gemsEconomy = FakeEconomyFacade(acceptsDeposits = true)
            val gemsReward = Reward.Money(
                minAmount = 3.0,
                maxAmount = 3.0,
                currencyId = "gems"
            )

            val paid = payer(defaultEconomy, mapOf("gems" to gemsEconomy)).invoke(winnerUuid, gemsReward)

            assertEquals(3, paid)
            assertEquals(mapOf(winnerUuid to 3.0), gemsEconomy.deposits)
            assertEquals(emptyMap(), defaultEconomy.deposits)
        }
    }

    @Test
    fun GIVEN_currency_without_economy_WHEN_paid_THEN_nothing_is_paid(): TestResult {
        return runTest {
            val defaultEconomy = FakeEconomyFacade(acceptsDeposits = true)
            val unknownCurrencyReward = Reward.Money(
                minAmount = 3.0,
                maxAmount = 3.0,
                currencyId = "gems"
            )

            val paid = payer(defaultEconomy).invoke(winnerUuid, unknownCurrencyReward)

            assertNull(paid)
            assertEquals(emptyMap(), defaultEconomy.deposits)
        }
    }

    @Test
    fun GIVEN_server_without_default_economy_WHEN_paid_THEN_nothing_is_paid(): TestResult {
        return runTest {
            assertNull(payer(defaultEconomy = null).invoke(winnerUuid, fixedReward))
        }
    }

    @Test
    fun GIVEN_economy_that_refuses_the_deposit_WHEN_paid_THEN_nothing_is_paid(): TestResult {
        return runTest {
            val refusingEconomy = FakeEconomyFacade(acceptsDeposits = false)

            val paid = payer(refusingEconomy).invoke(winnerUuid, fixedReward)

            assertNull(paid)
            assertEquals(emptyMap(), refusingEconomy.deposits)
        }
    }
}
