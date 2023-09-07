package ru.astrainteractive.aspekt.util

import com.earth2me.essentials.api.Economy
import ru.astrainteractive.astralibs.economy.EconomyProvider
import java.math.BigDecimal
import java.util.UUID

class EssentialsEconomyProvider : EconomyProvider {
    override fun addMoney(uuid: UUID, amount: Double): Boolean {
        runCatching {
            Economy.add(uuid, BigDecimal(amount))
        }.onFailure {
            it.printStackTrace()
        }
        return true
    }

    override fun getBalance(uuid: UUID): Double? {
        return runCatching {
            Economy.getMoneyExact(uuid).toDouble()
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }

    override fun hasAtLeast(uuid: UUID, amount: Double): Boolean {
        return runCatching {
            Economy.hasMore(uuid, BigDecimal(amount))
        }.onFailure {
            it.printStackTrace()
        }.getOrNull() ?: false
    }

    override fun takeMoney(uuid: UUID, amount: Double): Boolean {
        if (!hasAtLeast(uuid, amount)) return false
        runCatching {
            Economy.subtract(uuid, BigDecimal(amount))
        }.onFailure {
            it.printStackTrace()
        }
        return true
    }
}
