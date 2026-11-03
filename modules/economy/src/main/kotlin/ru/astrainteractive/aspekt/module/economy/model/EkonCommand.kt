package ru.astrainteractive.aspekt.module.economy.model

import ru.astrainteractive.astralibs.command.api.brigadier.sender.KCommandSender
import ru.astrainteractive.astralibs.server.player.KPlayer

internal sealed interface EkonCommand {
    data class ListCurrencies(val sender: KCommandSender) : EkonCommand
    data class Top(
        val sender: KCommandSender,
        val currency: Currency,
        val page: Int
    ) : EkonCommand

    data class Balance(
        val sender: KCommandSender,
        val otherPlayer: KPlayer,
        val currency: Currency
    ) : EkonCommand

    data class Set(
        val sender: KCommandSender,
        val otherPlayer: KPlayer,
        val currency: Currency,
        val amount: Double
    ) : EkonCommand

    data class Add(
        val sender: KCommandSender,
        val otherPlayer: KPlayer,
        val currency: Currency,
        val amount: Double
    ) : EkonCommand
}
