package ru.astrainteractive.aspekt.module.economy.command.ekon.argument

import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentConverter
import ru.astrainteractive.astralibs.command.api.exception.CommandException

internal class CurrencyArgument(private val currencies: List<CurrencyModel>) : ArgumentConverter<CurrencyModel> {

    override fun transform(argument: String): CurrencyModel {
        return currencies.find { currency -> currency.name == argument }
            ?: throw CurrencyNotFoundException(argument)
    }

    data class CurrencyNotFoundException(val name: String) : CommandException("Currency not found: $name")
}
