package ru.astrainteractive.aspekt.module.chatgame.service

import ru.astrainteractive.aspekt.di.factory.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.model.randomAmount
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID
import kotlin.random.Random

/** Pays a [Reward.Money] in the currency it names, or in the server's default currency when it names none. */
internal class MoneyRewardPayer(
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val random: Random
) : Logger by JUtiltLogger("AspeKt-MoneyRewardPayer") {

    private fun findEconomy(currencyId: String?): EconomyFacade? {
        return when (currencyId) {
            null -> currencyEconomyProviderFactory.findDefault()
            else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
        }
    }

    /**
     * @return the coins paid, or `null` when no economy serves the reward's currency or the economy refused the
     * deposit; both cases are logged here
     */
    suspend fun pay(playerUuid: UUID, reward: Reward.Money): Int? {
        val currencyName = reward.currencyId ?: "default"
        val economy = findEconomy(reward.currencyId) ?: run {
            error { "#pay no economy serves the $currencyName currency, the reward of $playerUuid is not paid" }
            return null
        }
        val amount = reward.randomAmount(random)
        if (!economy.addMoney(playerUuid, amount.toDouble())) {
            error { "#pay the $currencyName economy refused to pay $amount to $playerUuid" }
            return null
        }
        return amount
    }
}
