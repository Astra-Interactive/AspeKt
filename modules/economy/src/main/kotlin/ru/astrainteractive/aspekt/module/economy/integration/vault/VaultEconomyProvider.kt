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
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import java.text.DecimalFormat
import kotlin.time.Duration.Companion.seconds

@Suppress("TooManyFunctions")
internal class VaultEconomyProvider(
    private val primaryCurrencyModel: CurrencyModel,
    private val dao: EconomyDao
) : Economy, Logger by JUtiltLogger("Economy-${primaryCurrencyModel.id}") {
    /**
     * Run blocking function but try not to block main thread
     */
    private fun <T> runIoBlocking(block: suspend CoroutineScope.() -> T): T {
        return if (Bukkit.isPrimaryThread()) {
            runBlocking(Dispatchers.IO) {
                flow<T> { emit(block.invoke(this@runBlocking)) }
                    .timeout(timeout = 15.seconds)
                    .first()
            }
        } else {
            runBlocking(block = block)
        }
    }

    private fun notImplemented(message: String): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, message)
    }

    private fun requireOfflinePlayer(name: String?): OfflinePlayer {
        name ?: error("The requested name is null")
        val offlinePlayer = Bukkit.getOfflinePlayer(name)
        return offlinePlayer
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

    override fun hasAccount(playerName: String?): Boolean = hasAccount(requireOfflinePlayer(playerName))

    override fun hasAccount(player: OfflinePlayer?): Boolean = runIoBlocking {
        player ?: return@runIoBlocking false
        dao.findPlayerCurrency(player.uniqueId.toString(), currencyId = primaryCurrencyModel.id) != null
    }

    override fun hasAccount(playerName: String?, worldName: String?): Boolean {
        return hasAccount(requireOfflinePlayer(playerName), worldName)
    }

    override fun hasAccount(player: OfflinePlayer?, p1: String?): Boolean = hasAccount(player)

    override fun getBalance(playerName: String?): Double = getBalance(requireOfflinePlayer(playerName))

    override fun getBalance(player: OfflinePlayer?): Double = runIoBlocking {
        player ?: return@runIoBlocking 0.0
        dao.findPlayerCurrency(player.uniqueId.toString(), currencyId = primaryCurrencyModel.id)?.balance ?: 0.0
    }

    override fun getBalance(playerName: String?, world: String?): Double {
        return getBalance(requireOfflinePlayer(playerName), world)
    }

    override fun getBalance(player: OfflinePlayer?, p1: String?): Double = getBalance(player)

    override fun has(playerName: String?, amount: Double): Boolean {
        return has(requireOfflinePlayer(playerName), amount)
    }

    override fun has(p0: OfflinePlayer?, amount: Double): Boolean = getBalance(p0) >= amount

    override fun has(playerName: String?, worldName: String?, amount: Double): Boolean {
        return has(requireOfflinePlayer(playerName), worldName, amount)
    }

    override fun has(p0: OfflinePlayer?, p1: String?, amount: Double): Boolean {
        return getBalance(p0) >= amount
    }

    override fun withdrawPlayer(playerName: String?, amount: Double): EconomyResponse {
        return withdrawPlayer(requireOfflinePlayer(playerName), amount)
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

    override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
        return withdrawPlayer(requireOfflinePlayer(playerName), worldName, amount)
    }

    override fun withdrawPlayer(p0: OfflinePlayer?, p1: String?, p2: Double): EconomyResponse = withdrawPlayer(p0, p2)

    override fun depositPlayer(playerName: String?, amount: Double): EconomyResponse {
        return depositPlayer(requireOfflinePlayer(playerName), amount)
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

    override fun depositPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse {
        return depositPlayer(requireOfflinePlayer(playerName), worldName, amount)
    }

    override fun depositPlayer(player: OfflinePlayer?, p1: String?, amount: Double): EconomyResponse {
        return depositPlayer(player, amount)
    }

    override fun createBank(p0: String?, p1: String?): EconomyResponse = notImplemented("Banks not implemented")

    override fun createBank(p0: String?, p1: OfflinePlayer?): EconomyResponse = notImplemented("Banks not implemented")

    override fun deleteBank(p0: String?): EconomyResponse = notImplemented("Banks not implemented")

    override fun bankBalance(p0: String?): EconomyResponse = notImplemented("Banks not implemented")

    override fun bankHas(p0: String?, p1: Double): EconomyResponse = notImplemented("Banks not implemented")

    override fun bankWithdraw(p0: String?, p1: Double): EconomyResponse = notImplemented("Banks not implemented")

    override fun bankDeposit(p0: String?, p1: Double): EconomyResponse = notImplemented("Banks not implemented")

    override fun isBankOwner(p0: String?, p1: String?): EconomyResponse = notImplemented("Banks not implemented")

    override fun isBankOwner(p0: String?, p1: OfflinePlayer?): EconomyResponse = notImplemented("Banks not implemented")

    override fun isBankMember(p0: String?, p1: String?): EconomyResponse = notImplemented("Banks not implemented")

    override fun isBankMember(p0: String?, p1: OfflinePlayer?): EconomyResponse {
        return notImplemented("Banks not implemented")
    }

    override fun getBanks(): MutableList<String> = mutableListOf()

    override fun createPlayerAccount(playerName: String?): Boolean {
        return createPlayerAccount(requireOfflinePlayer(playerName))
    }

    override fun createPlayerAccount(player: OfflinePlayer?): Boolean {
        player ?: return false
        if (hasAccount(player)) return false
        return depositPlayer(player, 0.0).transactionSuccess()
    }

    override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean {
        return createPlayerAccount(requireOfflinePlayer(playerName), worldName)
    }

    override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?): Boolean {
        return createPlayerAccount(player)
    }
}
