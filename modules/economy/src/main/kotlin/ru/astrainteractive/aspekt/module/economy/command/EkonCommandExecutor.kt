package ru.astrainteractive.aspekt.module.economy.command

import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.core.plugin.model.PluginTranslation
import ru.astrainteractive.aspekt.core.plugin.util.balance
import ru.astrainteractive.aspekt.core.plugin.util.currencies
import ru.astrainteractive.aspekt.core.plugin.util.topItem
import ru.astrainteractive.aspekt.module.economy.api.EconomyRepository
import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.aspekt.module.economy.model.EconomyPlayer
import ru.astrainteractive.aspekt.module.economy.model.EkonCommand
import ru.astrainteractive.aspekt.module.economy.model.PlayerCurrency
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

internal class EkonCommandExecutor(
    translationKrate: CachedKrate<PluginTranslation>,
    dispatchers: KotlinDispatchers,
    private val repository: EconomyRepository
) : CoroutineFeature by CoroutineFeature.Default(dispatchers.IO).withTimings(),
    Logger by JUtiltLogger("EkonCommandExecutor") {
    private val translation by translationKrate

    private suspend fun addCurrency(input: EkonCommand.Add) {
        val playerCurrency = repository.findPlayerCurrency(
            playerUuid = input.otherPlayer.uuid.toString(),
            currencyId = input.currency.id
        ) ?: PlayerCurrency(
            playerModel = EconomyPlayer(
                name = input.otherPlayer.name.orEmpty(),
                uuid = input.otherPlayer.uuid.toString()
            ),
            balance = 0.0,
            currencyModel = input.currency
        )

        kotlin.runCatching {
            val updatedCurrency = playerCurrency.copy(balance = playerCurrency.balance + input.amount)
            repository.updatePlayerCurrency(updatedCurrency)
        }.propagateCancellationException().onFailure { throwable ->
            error { "#execute_Add: ${throwable.message}" }
            input.sender.sendMessage(translation.economy.transferFailed)
        }.onSuccess { input.sender.sendMessage(translation.economy.transferred) }
    }

    private suspend fun setCurrency(input: EkonCommand.Set) {
        kotlin.runCatching {
            val updatedCurrency = PlayerCurrency(
                playerModel = EconomyPlayer(
                    name = input.otherPlayer.name.orEmpty(),
                    uuid = input.otherPlayer.uuid.toString()
                ),
                balance = input.amount,
                currencyModel = input.currency
            )
            repository.updatePlayerCurrency(updatedCurrency)
        }.propagateCancellationException().onFailure { throwable ->
            error { "#execute_Add: ${throwable.message}" }
            input.sender.sendMessage(translation.economy.transferFailed)
        }.onSuccess { input.sender.sendMessage(translation.economy.transferred) }
    }

    private suspend fun balance(input: EkonCommand.Balance) {
        val amount = repository.findPlayerCurrency(
            playerUuid = input.otherPlayer.uuid.toString(),
            currencyId = input.currency.id
        )?.balance ?: 0.0
        input.sender.sendMessage(translation.economy.balance(amount))
    }

    private suspend fun listCurrencies(input: EkonCommand.ListCurrencies) {
        val currencies = repository.loadAllCurrencies()
            .map(Currency::name)
            .joinToString(",")

        input.sender.sendMessage(translation.economy.currencies(currencies))
    }

    private suspend fun topPlayers(input: EkonCommand.Top) {
        val top5 = repository.topCurrency(
            id = input.currency.id,
            page = input.page,
            size = 5
        )
        if (top5.isEmpty()) {
            input.sender.sendMessage(translation.economy.topEmpty)
        } else {
            input.sender.sendMessage(translation.economy.topTitle)
            top5.forEachIndexed { i, topItem ->
                input.sender.sendMessage(
                    translation.economy.topItem(
                        index = i + 1,
                        name = topItem.playerModel.name,
                        balance = topItem.balance
                    )
                )
            }
        }
    }

    fun execute(input: EkonCommand) {
        launch {
            when (input) {
                is EkonCommand.Add -> {
                    addCurrency(input)
                }

                is EkonCommand.Set -> {
                    setCurrency(input)
                }

                is EkonCommand.Balance -> {
                    balance(input)
                }

                is EkonCommand.ListCurrencies -> {
                    listCurrencies(input)
                }

                is EkonCommand.Top -> {
                    topPlayers(input)
                }
            }
        }
    }
}
