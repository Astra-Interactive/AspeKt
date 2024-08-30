package ru.astrainteractive.aspekt.module.economy.command.ekon

import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel

internal interface EkonCommand {
    sealed interface Model {
        data class ListCurrencies(val sender: CommandSender) : Model
        data class Top(
            val sender: CommandSender,
            val currency: CurrencyModel,
            val page: Int
        ) : Model

        data class Balance(
            val sender: CommandSender,
            val otherPlayer: OfflinePlayer,
            val currency: CurrencyModel
        ) : Model

        data class Set(
            val sender: CommandSender,
            val otherPlayer: OfflinePlayer,
            val currency: CurrencyModel,
            val amount: Double
        ) : Model

        data class Add(
            val sender: CommandSender,
            val otherPlayer: OfflinePlayer,
            val currency: CurrencyModel,
            val amount: Double
        ) : Model
    }
    companion object {
        const val ALIAS = "ekon"
    }
}
