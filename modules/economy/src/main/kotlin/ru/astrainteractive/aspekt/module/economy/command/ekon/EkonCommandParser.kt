package ru.astrainteractive.aspekt.module.economy.command.ekon

import ru.astrainteractive.aspekt.module.economy.command.ekon.argument.CurrencyArgument
import ru.astrainteractive.aspekt.module.economy.command.ekon.argument.OfflinePlayerArgument
import ru.astrainteractive.aspekt.module.economy.database.dao.CachedDao
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.astralibs.command.api.argumenttype.PrimitiveArgumentType
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContext
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.argumentOrElse
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requireArgument
import ru.astrainteractive.astralibs.command.api.context.BukkitCommandContextExt.requirePermission
import ru.astrainteractive.astralibs.command.api.exception.DefaultCommandException
import ru.astrainteractive.astralibs.command.api.parser.CommandParser

internal class EkonCommandParser(
    private val cachedDao: CachedDao
) : CommandParser<EkonCommand.Model, BukkitCommandContext> {

    override fun parse(commandContext: BukkitCommandContext): EkonCommand.Model {
        val sender = commandContext.sender
        val args = commandContext.args

        commandContext.requirePermission(PluginPermission.AdminClaim)
        return when (val arg = args.getOrNull(0)) {
            "list" -> {
                EkonCommand.Model.ListCurrencies(sender)
            }

            "top" -> {
                val currency = commandContext.requireArgument(
                    index = 1,
                    type = CurrencyArgument(cachedDao.getAllCurrencies())
                )
                val page = commandContext.argumentOrElse(
                    index = 2,
                    type = PrimitiveArgumentType.Int,
                    default = { 0 }
                )
                EkonCommand.Model.Top(sender, currency, page)
            }

            "balance" -> {
                val currency = commandContext.requireArgument(
                    index = 1,
                    type = CurrencyArgument(cachedDao.getAllCurrencies())
                )
                val otherPlayer = commandContext.requireArgument(
                    index = 2,
                    type = OfflinePlayerArgument
                )
                EkonCommand.Model.Balance(sender, otherPlayer, currency)
            }

            "set", "add" -> {
                commandContext.requirePermission(PluginPermission.Economy.SetBalance)
                val currency = commandContext.requireArgument(
                    index = 1,
                    type = CurrencyArgument(cachedDao.getAllCurrencies())
                )
                val otherPlayer = commandContext.requireArgument(
                    index = 2,
                    type = OfflinePlayerArgument
                )
                val amount = commandContext.requireArgument(
                    index = 3,
                    type = PrimitiveArgumentType.Double
                )
                if (arg == "set") {
                    EkonCommand.Model.Set(sender, otherPlayer, currency, amount)
                } else {
                    EkonCommand.Model.Add(sender, otherPlayer, currency, amount)
                }
            }

            else -> throw DefaultCommandException.BadArgumentException(args.getOrNull(0), PrimitiveArgumentType.String)
        }
    }
}
