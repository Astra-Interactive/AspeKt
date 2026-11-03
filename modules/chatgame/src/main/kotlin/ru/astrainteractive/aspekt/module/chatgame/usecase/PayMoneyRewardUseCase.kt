package ru.astrainteractive.aspekt.module.chatgame.usecase

import ru.astrainteractive.aspekt.core.economy.api.CurrencyEconomyProviderFactory
import ru.astrainteractive.aspekt.module.chatgame.model.Reward
import ru.astrainteractive.aspekt.module.chatgame.util.randomAmount
import ru.astrainteractive.astralibs.economy.EconomyFacade
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.util.UUID
import kotlin.random.Random

internal class PayMoneyRewardUseCase(
    private val currencyEconomyProviderFactory: CurrencyEconomyProviderFactory,
    private val random: Random
) : Logger by JUtiltLogger("AspeKt-PayMoneyRewardUseCase") {

    private fun findEconomy(currencyId: String?): EconomyFacade? {
        return when (currencyId) {
            null -> currencyEconomyProviderFactory.findDefault()
            else -> currencyEconomyProviderFactory.findByCurrencyId(currencyId)
        }
    }

    suspend operator fun invoke(playerUuid: UUID, reward: Reward.Money): Int? {
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
