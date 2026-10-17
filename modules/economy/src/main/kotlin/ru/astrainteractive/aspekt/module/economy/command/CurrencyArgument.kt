package ru.astrainteractive.aspekt.module.economy.command

import ru.astrainteractive.aspekt.module.economy.model.Currency
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentConverter
import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal class CurrencyArgument(private val currencies: List<Currency>) : ArgumentConverter<Currency> {

    override fun transform(argument: String): Currency {
        return currencies.find { currency -> currency.name == argument }
            ?: throw CurrencyNotFoundException(argument)
    }

    data class CurrencyNotFoundException(val name: String) : CommandException("Currency not found: $name")
}
