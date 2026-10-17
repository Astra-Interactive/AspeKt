package ru.astrainteractive.aspekt.module.economy.command

import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.astralibs.command.api.brigadier.sender.KCommandSender
import ru.astrainteractive.astralibs.server.player.KPlayer

internal interface EkonCommand {
    sealed interface Model {
        data class ListCurrencies(val sender: KCommandSender) : Model
        data class Top(
            val sender: KCommandSender,
            val currency: Currency,
            val page: Int
        ) : Model

        data class Balance(
            val sender: KCommandSender,
            val otherPlayer: KPlayer,
            val currency: Currency
        ) : Model

        data class Set(
            val sender: KCommandSender,
            val otherPlayer: KPlayer,
            val currency: Currency,
            val amount: Double
        ) : Model

        data class Add(
            val sender: KCommandSender,
            val otherPlayer: KPlayer,
            val currency: Currency,
            val amount: Double
        ) : Model
    }
}
