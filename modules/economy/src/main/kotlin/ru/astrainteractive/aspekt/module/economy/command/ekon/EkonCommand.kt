package ru.astrainteractive.aspekt.module.economy.command.ekon

import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KCommandSender
import ru.astrainteractive.astralibs.server.player.KPlayer

internal interface EkonCommand {
    sealed interface Model {
        data class ListCurrencies(val sender: KCommandSender) : Model
        data class Top(
            val sender: KCommandSender,
            val currency: CurrencyModel,
            val page: Int
        ) : Model

        data class Balance(
            val sender: KCommandSender,
            val otherPlayer: KPlayer,
            val currency: CurrencyModel
        ) : Model

        data class Set(
            val sender: KCommandSender,
            val otherPlayer: KPlayer,
            val currency: CurrencyModel,
            val amount: Double
        ) : Model

        data class Add(
            val sender: KCommandSender,
            val otherPlayer: KPlayer,
            val currency: CurrencyModel,
            val amount: Double
        ) : Model
    }
}
