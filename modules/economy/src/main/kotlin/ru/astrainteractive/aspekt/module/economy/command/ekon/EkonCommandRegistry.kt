package ru.astrainteractive.aspekt.module.economy.command.ekon

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.economy.command.ekon.argument.CurrencyArgument
import ru.astrainteractive.aspekt.module.economy.command.ekon.argument.OfflinePlayerArgument
import ru.astrainteractive.aspekt.module.economy.database.dao.EconomyDao
import ru.astrainteractive.aspekt.module.economy.model.CurrencyModel
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.exception.ArgumentTypeException
import ru.astrainteractive.astralibs.command.api.exception.BadArgumentException
import ru.astrainteractive.astralibs.command.api.exception.NoPermissionException
import ru.astrainteractive.astralibs.command.api.util.PluginExt.registerCommand
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.util.StringListExt.withEntry

internal class EkonCommandRegistry(
    private val plugin: JavaPlugin,
    private val getCurrencies: () -> List<CurrencyModel>,
    private val getKyori: () -> KyoriComponentSerializer,
    private val getTranslation: () -> PluginTranslation,
    private val dao: EconomyDao
) : Logger by JUtiltLogger("EkonCommandRegistry") {
    private val currencies get() = getCurrencies.invoke()
    private val kyori get() = getKyori.invoke()
    private val translation get() = getTranslation.invoke()

    private fun adminPrivateCompleter() =
        plugin.getCommand(EkonCommand.ALIAS)?.setTabCompleter { sender, command, label, args ->
            when {
                args.size <= 1 -> listOf("list", "top", "balance", "set").withEntry(args.getOrNull(0))

                else -> {
                    when (val arg0 = args.getOrNull(0)) {
                        "list" -> emptyList()
                        "top" -> currencies.map(CurrencyModel::name).withEntry(arg0)

                        "balance" -> {
                            when (val arg1 = args.getOrNull(1)) {
                                null -> currencies.map(CurrencyModel::name).withEntry(arg0)
                                else -> Bukkit.getOnlinePlayers().map(Player::getName).withEntry(arg1)
                            }
                        }

                        "set", "add" -> {
                            when (val arg1 = args.getOrNull(1)) {
                                null -> currencies.map(CurrencyModel::name).withEntry(arg0)
                                else -> {
                                    when (arg1.getOrNull(2)) {
                                        null -> Bukkit.getOnlinePlayers().map(Player::getName).withEntry(arg1)
                                        else -> listOf("123")
                                    }
                                }
                            }
                        }

                        else -> emptyList()
                    }
                }
            }
        }

    fun register() {
        adminPrivateCompleter()
        plugin.registerCommand(
            alias = EkonCommand.ALIAS,
            commandParser = EkonCommandParser(
                getCurrencies = getCurrencies
            ),
            commandExecutor = EkonCommandExecutor(
                getKyori = getKyori,
                getTranslation = getTranslation,
                dao = dao
            ),
            errorHandler = { context, throwable ->
                when (throwable) {
                    is BadArgumentException -> with(kyori) {
                        context.sender.sendMessage(translation.general.wrongUsage.component)
                    }

                    is ArgumentTypeException -> with(kyori) {
                        context.sender.sendMessage(translation.general.wrongUsage.component)
                    }

                    is CurrencyArgument.CurrencyNotFoundException -> with(kyori) {
                        context.sender.sendMessage(translation.general.wrongUsage.component)
                    }

                    is OfflinePlayerArgument.PlayerNotFound -> with(kyori) {
                        context.sender.sendMessage(translation.general.wrongUsage.component)
                    }

                    is NoPermissionException -> with(kyori) {
                        context.sender.sendMessage(translation.general.noPermission.component)
                    }

                    else -> with(kyori) {
                        error { "#errorHandler handler for ${throwable::class} not found" }
                        context.sender.sendMessage(translation.general.noPermission.component)
                    }
                }
            }
        )
    }
}
