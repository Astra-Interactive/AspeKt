package ru.astrainteractive.aspekt.module.economy.command.ekon.argument

import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentType
import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal class CurrencyArgument(private val currencies: List<CurrencyModel>) : ArgumentType<CurrencyModel> {
    override val key: String = "CurrencyArgument"

    override fun transform(value: String): CurrencyModel {
        return currencies.find { currency -> currency.name == value }
            ?: throw CurrencyNotFoundException(value)
    }

    data class CurrencyNotFoundException(val name: String) : CommandException("Currency not found: $name")
}
