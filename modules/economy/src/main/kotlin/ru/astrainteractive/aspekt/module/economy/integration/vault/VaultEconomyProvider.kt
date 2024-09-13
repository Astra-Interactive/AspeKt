package ru.astrainteractive.aspekt.module.economy.integration.vault

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.runBlocking
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.aspekt.module.economy.model.PlayerModel
import java.text.DecimalFormat
import kotlin.time.Duration.Companion.seconds

@Suppress("TooManyFunctions")
internal class VaultEconomyProvider(
    private val primaryCurrencyModel: CurrencyModel,
    private val dao: EconomyDao
) : Economy {
    /**
     * Run blocking function but try not to block main thread
     */
    private fun <T> runIoBlocking(block: suspend CoroutineScope.() -> T): T {
        return if (Bukkit.isPrimaryThread()) {
            runBlocking(Dispatchers.IO) {
                flow<T> { block.invoke(this@runBlocking) }
                    .timeout(timeout = 15.seconds)
                    .first()
            }
        } else {
            runBlocking(block = block)
        }
    }

    override fun isEnabled(): Boolean = true

    override fun getName(): String = "AspeKt Economy of ${primaryCurrencyModel.id} currency"

    override fun hasBankSupport(): Boolean = false

    override fun fractionalDigits(): Int = -1

    override fun format(amount: Double): String {
        return DecimalFormat("#.00").format(amount)
    }

    override fun currencyNamePlural(): String = primaryCurrencyModel.name

    override fun currencyNameSingular(): String = primaryCurrencyModel.name

    override fun hasAccount(p0: String?): Boolean = error("Method with player name is not supported")

    override fun hasAccount(player: OfflinePlayer?): Boolean = runIoBlocking {
        player ?: return@runIoBlocking false
        dao.findPlayerCurrency(player.uniqueId.toString(), currencyId = primaryCurrencyModel.id) != null
    }

    override fun hasAccount(p0: String?, p1: String?): Boolean = error("Method with player name is not supported")

    override fun hasAccount(player: OfflinePlayer?, p1: String?): Boolean = hasAccount(player)

    override fun getBalance(p0: String?): Double = error("Method with player name is not supported")

    override fun getBalance(player: OfflinePlayer?): Double = runIoBlocking {
        player ?: return@runIoBlocking 0.0
        dao.findPlayerCurrency(player.uniqueId.toString(), currencyId = primaryCurrencyModel.id)?.balance ?: 0.0
    }

    override fun getBalance(p0: String?, p1: String?): Double = error("Method with player name is not supported")

    override fun getBalance(player: OfflinePlayer?, p1: String?): Double = getBalance(player)

    override fun has(p0: String?, p1: Double): Boolean = error("Method with player name is not supported")

    override fun has(p0: OfflinePlayer?, amount: Double): Boolean = getBalance(p0) >= amount

    override fun has(p0: String?, p1: String?, p2: Double): Boolean = error("Method with player name is not supported")

    override fun has(p0: OfflinePlayer?, p1: String?, amount: Double): Boolean = getBalance(p0) >= amount

    override fun withdrawPlayer(p0: String?, p1: Double): EconomyResponse {
        error("Method with player name is not supported")
    }

    override fun withdrawPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        val playerName = player?.name
        if (player == null || playerName.isNullOrBlank()) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player is null")
        }
        val playerCurrency = runIoBlocking {
            dao.findPlayerCurrency(player.uniqueId.toString(), currencyId = primaryCurrencyModel.id)
        }
        if (playerCurrency == null) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player is null")
        }
        if (playerCurrency.balance - amount < 0) {
            return EconomyResponse(
                amount,
                playerCurrency.balance,
                EconomyResponse.ResponseType.FAILURE,
                "Player doesn't have enough amount to withdraw"
            )
        }
        runIoBlocking { dao.updatePlayerCurrency(playerCurrency.copy(balance = playerCurrency.balance - amount)) }
        return EconomyResponse(
            amount,
            playerCurrency.balance - amount,
            EconomyResponse.ResponseType.SUCCESS,
            null
        )
    }

    override fun withdrawPlayer(p0: String?, p1: String?, p2: Double): EconomyResponse {
        error("Method with player name is not supported")
    }

    override fun withdrawPlayer(p0: OfflinePlayer?, p1: String?, p2: Double): EconomyResponse = withdrawPlayer(p0, p2)

    override fun depositPlayer(p0: String?, p1: Double): EconomyResponse {
        error("Method with player name is not supported")
    }

    override fun depositPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        val playerName = player?.name
        if (player == null || playerName.isNullOrBlank()) {
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Player is null")
        }
        val playerCurrency = runIoBlocking {
            dao.findPlayerCurrency(player.uniqueId.toString(), currencyId = primaryCurrencyModel.id) ?: PlayerCurrency(
                playerModel = PlayerModel(
                    name = playerName,
                    uuid = player.uniqueId.toString()
                ),
                balance = 0.0,
                currencyModel = primaryCurrencyModel
            )
        }
        runIoBlocking { dao.updatePlayerCurrency(playerCurrency.copy(balance = playerCurrency.balance + amount)) }
        return EconomyResponse(
            amount,
            playerCurrency.balance + amount,
            EconomyResponse.ResponseType.SUCCESS,
            null
        )
    }

    override fun depositPlayer(p0: String?, p1: String?, p2: Double): EconomyResponse {
        error("Method with player name is not supported")
    }

    override fun depositPlayer(player: OfflinePlayer?, p1: String?, amount: Double): EconomyResponse =
        depositPlayer(player, amount)

    override fun createBank(p0: String?, p1: String?): EconomyResponse = error("Banks not implemented")

    override fun createBank(p0: String?, p1: OfflinePlayer?): EconomyResponse = error("Banks not implemented")

    override fun deleteBank(p0: String?): EconomyResponse = error("Banks not implemented")

    override fun bankBalance(p0: String?): EconomyResponse = error("Banks not implemented")

    override fun bankHas(p0: String?, p1: Double): EconomyResponse = error("Banks not implemented")

    override fun bankWithdraw(p0: String?, p1: Double): EconomyResponse = error("Banks not implemented")

    override fun bankDeposit(p0: String?, p1: Double): EconomyResponse = error("Banks not implemented")

    override fun isBankOwner(p0: String?, p1: String?): EconomyResponse = error("Banks not implemented")

    override fun isBankOwner(p0: String?, p1: OfflinePlayer?): EconomyResponse = error("Banks not implemented")

    override fun isBankMember(p0: String?, p1: String?): EconomyResponse = error("Banks not implemented")

    override fun isBankMember(p0: String?, p1: OfflinePlayer?): EconomyResponse = error("Banks not implemented")

    override fun getBanks(): MutableList<String> = error("Banks not implemented")

    override fun createPlayerAccount(p0: String?): Boolean = error("Creating accounts not implemented")

    override fun createPlayerAccount(p0: OfflinePlayer?): Boolean = error("Creating accounts not implemented")

    override fun createPlayerAccount(p0: String?, p1: String?): Boolean = error("Creating accounts not implemented")

    override fun createPlayerAccount(p0: OfflinePlayer?, p1: String?): Boolean {
        error("Creating accounts not implemented")
    }
}
