package ru.astrainteractive.aspekt.module.economy.command.ekon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.aspekt.module.economy.model.PlayerModel
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger

internal class EkonCommandExecutor(
    private val getKyori: () -> KyoriComponentSerializer,
    private val getTranslation: () -> PluginTranslation,
    private val dao: EconomyDao
) : CommandExecutor<EkonCommand.Model>,
    CoroutineFeature by CoroutineFeature.Default(
        Dispatchers.IO
    ),
    Logger by JUtiltLogger("EkonCommandExecutor") {
    private val kyori get() = getKyori.invoke()
    private val translation get() = getTranslation.invoke()

    private suspend fun addCurrency(input: EkonCommand.Model.Add) = with(kyori) {
        val playerCurrency = dao.findPlayerCurrency(
            playerUuid = input.otherPlayer.uniqueId.toString(),
            currencyId = input.currency.id
        ) ?: PlayerCurrency(
            playerModel = PlayerModel(
                name = input.otherPlayer.name.orEmpty(),
                uuid = input.otherPlayer.uniqueId.toString()
            ),
            balance = 0.0,
            currencyModel = input.currency
        )

        kotlin.runCatching {
            val updatedCurrency = playerCurrency.copy(balance = playerCurrency.balance + input.amount)
            dao.updatePlayerCurrency(updatedCurrency)
        }.onFailure {
            error { "#execute_Add: ${it.message}" }
            input.sender.sendMessage(translation.economy.errorTransferMoney.component)
        }.onSuccess { input.sender.sendMessage(translation.economy.moneyTransferred.component) }
    }

    private suspend fun setCurrency(input: EkonCommand.Model.Set) = with(kyori) {
        kotlin.runCatching {
            val updatedCurrency = PlayerCurrency(
                playerModel = PlayerModel(
                    name = input.otherPlayer.name.orEmpty(),
                    uuid = input.otherPlayer.uniqueId.toString()
                ),
                balance = input.amount,
                currencyModel = input.currency
            )
            dao.updatePlayerCurrency(updatedCurrency)
        }.onFailure {
            error { "#execute_Add: ${it.message}" }
            input.sender.sendMessage(translation.economy.errorTransferMoney.component)
        }.onSuccess { input.sender.sendMessage(translation.economy.moneyTransferred.component) }
    }

    private suspend fun balance(input: EkonCommand.Model.Balance) = with(kyori) {
        val amount = dao.findPlayerCurrency(
            playerUuid = input.otherPlayer.uniqueId.toString(),
            currencyId = input.currency.id
        )?.balance ?: 0.0
        input.sender.sendMessage(translation.economy.playerBalance(amount).component)
    }

    private suspend fun listCurrencies(input: EkonCommand.Model.ListCurrencies) = with(kyori) {
        val currencies = dao.getAllCurrencies()
            .map(CurrencyModel::name)
            .joinToString(",")

        input.sender.sendMessage(translation.economy.currencies(currencies).component)
    }

    private suspend fun topPlayers(input: EkonCommand.Model.Top) = with(kyori) {
        val top5 = dao.topCurrency(
            id = input.currency.id,
            page = input.page,
            size = 5
        )
        if (top5.isEmpty()) {
            input.sender.sendMessage(translation.economy.topsEmpty.component)
        } else {
            input.sender.sendMessage(translation.economy.topsTitle.component)
            top5.forEachIndexed { i, topItem ->
                input.sender.sendMessage(
                    translation.economy.topItem(
                        index = i + 1,
                        name = topItem.playerModel.name,
                        balance = topItem.balance
                    ).component
                )
            }
        }
    }

    override fun execute(input: EkonCommand.Model) {
        launch {
            with(kyori) {
                when (input) {
                    is EkonCommand.Model.Add -> {
                        addCurrency(input)
                    }

                    is EkonCommand.Model.Set -> {
                        setCurrency(input)
                    }

                    is EkonCommand.Model.Balance -> {
                        balance(input)
                    }

                    is EkonCommand.Model.ListCurrencies -> {
                        listCurrencies(input)
                    }

                    is EkonCommand.Model.Top -> {
                        topPlayers(input)
                    }
                }
            }
        }
    }
}
