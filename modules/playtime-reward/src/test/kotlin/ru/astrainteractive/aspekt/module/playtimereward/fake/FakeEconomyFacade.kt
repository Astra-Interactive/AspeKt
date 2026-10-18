package ru.astrainteractive.aspekt.module.playtimereward.fake

import ru.astrainteractive.astralibs.economy.EconomyFacade
import java.util.UUID

internal class FakeEconomyFacade : EconomyFacade {
    val deposits = mutableMapOf<UUID, Double>()

    override suspend fun getBalance(uuid: UUID): Double? = deposits[uuid]

    override suspend fun takeMoney(uuid: UUID, amount: Double): Boolean = false

    override suspend fun addMoney(uuid: UUID, amount: Double): Boolean {
        deposits[uuid] = deposits.getOrDefault(uuid, 0.0) + amount
        return true
    }

    override suspend fun hasAtLeast(uuid: UUID, amount: Double): Boolean = deposits.getOrDefault(uuid, 0.0) >= amount
}
