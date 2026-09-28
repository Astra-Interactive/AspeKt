@file:Suppress("MaxLineLength", "MaximumLineLength", "LongParameterList")

package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.localization.component.LocalizableComponent
import ru.astrainteractive.astralibs.localization.component.PlaceholderReplacement
import ru.astrainteractive.astralibs.localization.component.replace
import ru.astrainteractive.astralibs.localization.component.replaceAll
import ru.astrainteractive.astralibs.localization.locale.MinecraftLocales
import ru.astrainteractive.astralibs.localization.text.LocalizedText
import java.text.DecimalFormat

/**
 * Texts of the plugin, grouped by the feature that sends them. A key missing from `translations.yml` keeps its
 * default; the `prefix` of a group is applied to the defaults of that group only.
 */
@Serializable
data class PluginTranslation(
    @SerialName("command_error")
    val commandError: CommandError = CommandError(),
    @SerialName("reload")
    val reload: Reload = Reload(),
    @SerialName("sit")
    val sit: Sit = Sit(),
    @SerialName("claim")
    val claim: Claim = Claim(),
    @SerialName("money_advancement")
    val moneyAdvancement: MoneyAdvancement = MoneyAdvancement(),
    @SerialName("money_drop")
    val moneyDrop: MoneyDrop = MoneyDrop(),
    @SerialName("newbee")
    val newBee: NewBee = NewBee(),
    @SerialName("swear")
    val swear: Swear = Swear(),
    @SerialName("chat_game")
    val chatGame: ChatGame = ChatGame(),
    @SerialName("economy")
    val economy: Economy = Economy(),
    @SerialName("menu")
    val menu: Menu = Menu(),
    @SerialName("jails")
    val jails: Jails = Jails(),
    @SerialName("homes")
    val homes: Homes = Homes(),
    @SerialName("tpa")
    val tpa: Tpa = Tpa(),
    @SerialName("rtp")
    val rtp: Rtp = Rtp(),
    @SerialName("playtime_reward")
    val playtimeReward: PlaytimeReward = PlaytimeReward(),
) {
    /** Failures any command can report. */
    @Serializable
    data class CommandError(
        @SerialName("prefix")
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BAspeKt&7] "),
        @SerialName("no_permission")
        val noPermission: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18You don't have permission!")
                translation(MinecraftLocales.RU_RU, "&#db2c18У вас нет прав!")
            }
        ),
        @SerialName("wrong_usage")
        val wrongUsage: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Wrong usage!")
                translation(MinecraftLocales.RU_RU, "&#db2c18Неверное использование!")
            }
        ),
        @SerialName("only_player_command")
        val onlyPlayerCommand: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18This command is for players only!")
                translation(MinecraftLocales.RU_RU, "&#db2c18Эта команда только для игроков!")
            }
        ),
        @SerialName("player_not_found")
        val playerNotFound: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Player not found!")
                translation(MinecraftLocales.RU_RU, "&#db2c18Игрок не найден!")
            }
        ),
        @SerialName("invalid_argument")
        val invalidArgument: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Invalid argument value!")
                translation(MinecraftLocales.RU_RU, "&#db2c18Неверное значение аргумента!")
            }
        ),
        @SerialName("unknown_error")
        val unknownError: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18The command failed with an unknown error")
                translation(MinecraftLocales.RU_RU, "&#db2c18Команда завершилась с неизвестной ошибкой")
            }
        ),
    )

    @Serializable
    data class Reload(
        @SerialName("prefix")
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BAspeKt&7] "),
        @SerialName("started")
        val started: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#dbbb18Reloading the plugin")
                translation(MinecraftLocales.RU_RU, "&#dbbb18Перезагрузка плагина")
            }
        ),
        @SerialName("completed")
        val completed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596Reload complete")
                translation(MinecraftLocales.RU_RU, "&#42f596Перезагрузка успешно завершена")
            }
        ),
    )

    @Serializable
    data class PlaytimeReward(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BНАГРАДА&7] "),
        @SerialName("rewarded")
        private val rewarded: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "&#42f596You received &6%amount% &#42f596coins for the time spent on the server!"
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "&#42f596Вы получили &6%amount% &#42f596монет за время, проведённое на сервере!"
                )
            }
        ),
    ) {
        fun rewarded(amount: Number): LocalizableComponent = rewarded.replace(
            "%amount%",
            DecimalFormat("0.00").format(amount)
        )
    }

    @Serializable
    data class Rtp(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BRTP&7] "),
        @SerialName("max_rtp_jobs")
        val maxJobs: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Too many teleports are running at once!")
                translation(MinecraftLocales.RU_RU, "Достигнуто максимальное количество одновременных телепортов!")
            }
        ),
        @SerialName("timeout")
        val timeout: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Wait a little before teleporting again!")
                translation(MinecraftLocales.RU_RU, "Подождите немного прежде чем телепортироваться!")
            }
        ),
        @SerialName("found_place")
        val foundPlace: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Found a place for you!")
                translation(MinecraftLocales.RU_RU, "Найдено место для вас!")
            }
        ),
        @SerialName("not_found_place")
        val notFoundPlace: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Could not find a place!")
                translation(MinecraftLocales.RU_RU, "Не удалось найти место!")
            }
        ),
        @SerialName("max_rtp_retries")
        val maxRetries: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "Could not find a safe place within the allowed number of attempts!"
                )
                translation(MinecraftLocales.RU_RU, "Не удалось найти безопасное место за отведённое число попыток!")
            }
        ),
        @SerialName("searching")
        val searching: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Looking for a place for you...")
                translation(MinecraftLocales.RU_RU, "Ищем для вас место...")
            }
        ),
        @SerialName("low_tick_time")
        private val lowTickTime: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "TPS is too low: %tps%!")
                translation(MinecraftLocales.RU_RU, "Слишком маленький TPS: %tps%!")
            }
        ),
        @SerialName("did_you_mean_tpr")
        val didYouMeanTpr: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Maybe you meant /tpr")
                translation(MinecraftLocales.RU_RU, "&#db2c18Возможно, вы хотели ввести /tpr")
            }
        ),
    ) {
        fun lowTickTime(tps: Double): LocalizableComponent = lowTickTime.replace("%tps%", tps.toInt().toString())
    }

    @Serializable
    data class Tpa(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BTPA&7] "),
        @SerialName("you_have_no_pending_tp")
        val youHaveNoPendingTp: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You are not waiting to teleport to anyone")
                translation(MinecraftLocales.RU_RU, "Вы не ожидаете телепортации к игроку")
            }
        ),
        @SerialName("request_cancelled")
        val requestCancelled: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Request cancelled")
                translation(MinecraftLocales.RU_RU, "Запрос отменен")
            }
        ),
        @SerialName("no_pending_tp_to_deny")
        val noPendingTpToDeny: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You have no active requests")
                translation(MinecraftLocales.RU_RU, "У вас нет активных запросов")
            }
        ),
        @SerialName("cant_tp_self")
        val cantTpSelf: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You cannot send a request to yourself")
                translation(MinecraftLocales.RU_RU, "Нельзя отправить запрос самому себе")
            }
        ),
        @SerialName("request_sent")
        val requestSent: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Request sent")
                translation(MinecraftLocales.RU_RU, "Запрос отправлен")
            }
        ),
        @SerialName("request_accepted")
        val requestAccepted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Request accepted")
                translation(MinecraftLocales.RU_RU, "Запрос принят")
            }
        ),
        @SerialName("request_denied")
        private val requestDenied: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%player% denied the request")
                translation(MinecraftLocales.RU_RU, "Игрок %player% отменил запрос")
            }
        ),
        @SerialName("request_tpa")
        private val requestTpa: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%player% wants to teleport you to them")
                translation(MinecraftLocales.RU_RU, "Игрок %player% хочет телепортировать вас к себе")
            }
        ),
        @SerialName("request_tpa_here")
        private val requestTpaHere: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%player% wants to teleport to you")
                translation(MinecraftLocales.RU_RU, "Игрок %player% хочет к вам телепортироваться")
            }
        ),
    ) {
        fun requestDenied(denier: String): LocalizableComponent = requestDenied.replace("%player%", denier)
        fun requestTpa(requester: String): LocalizableComponent = requestTpa.replace("%player%", requester)
        fun requestTpaHere(requester: String): LocalizableComponent = requestTpaHere.replace("%player%", requester)
    }

    @Serializable
    data class Homes(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BHOMES&7] "),
        @SerialName("homeCreated")
        val created: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Home created!")
                translation(MinecraftLocales.RU_RU, "Дом создан!")
            }
        ),
        @SerialName("homeOverridden")
        val overwritten: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Home overwritten!")
                translation(MinecraftLocales.RU_RU, "Дом перезаписан!")
            }
        ),
        @SerialName("homeAlreadyExists")
        val alreadyExists: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "A home with this name already exists! Add force to the end of the command to overwrite it"
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "Дом с таким именем уже существует! Добавьте force в конец команды, чтобы перезаписать его"
                )
            }
        ),
        @SerialName("homeLimitReached")
        private val limitReached: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You have reached the home limit: %limit%")
                translation(MinecraftLocales.RU_RU, "Вы достигли лимита домов: %limit%")
            }
        ),
        @SerialName("homeNotFound")
        val notFound: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Home not found!")
                translation(MinecraftLocales.RU_RU, "Такой дом не найден!")
            }
        ),
        @SerialName("homeDeleted")
        val deleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Home deleted!")
                translation(MinecraftLocales.RU_RU, "Дом удален!")
            }
        ),
        @SerialName("teleporting")
        val teleported: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You have been teleported home")
                translation(MinecraftLocales.RU_RU, "Вы были телепортированы домой")
            }
        ),
    ) {
        fun limitReached(limit: Int): LocalizableComponent = limitReached.replace("%limit%", limit.toString())
    }

    @Serializable
    data class Jails(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BJAIL&7] "),
        @SerialName("jailsList")
        private val list: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Jails: %jails%")
                translation(MinecraftLocales.RU_RU, "Список: %jails%")
            }
        ),
        @SerialName("jailCreatedSuccess")
        private val created: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Jail created: %jail%")
                translation(MinecraftLocales.RU_RU, "Тюрьма создана: %jail%")
            }
        ),
        @SerialName("jailCreatedFail")
        val creationFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Could not create the jail. See the console for details.")
                translation(MinecraftLocales.RU_RU, "Не удалось создать тюрьму. Смотрите консоль для подробностей.")
            }
        ),
        @SerialName("jailDeleteSuccess")
        private val deleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Jail deleted: %jail%")
                translation(MinecraftLocales.RU_RU, "Тюрьма удалена: %jail%")
            }
        ),
        @SerialName("jailDeleteFail")
        val deletionFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Could not delete the jail. See the console for details.")
                translation(MinecraftLocales.RU_RU, "Не удалось удалить тюрьму. Смотрите консоль для подробностей.")
            }
        ),
        @SerialName("inmateAddSuccess")
        private val inmateJailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%name% has been jailed in %jail%")
                translation(MinecraftLocales.RU_RU, "Заключенный %name% посажен в %jail%")
            }
        ),
        @SerialName("inmateAddFail")
        val jailingFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Could not jail the player. See the console for details.")
                translation(MinecraftLocales.RU_RU, "Не удалось посадить в тюрьму. Смотрите консоль для подробностей.")
            }
        ),
        @SerialName("inmateFreeSuccess")
        private val inmateReleased: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "%name% has been released")
                translation(MinecraftLocales.RU_RU, "Заключенный %name% освобожден")
            }
        ),
        @SerialName("inmateFreeFail")
        val releaseFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "Could not release the player from jail. See the console for details."
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "Не удалось освободить из тюрьмы. Смотрите консоль для подробностей."
                )
            }
        ),
        @SerialName("jailHasInmates")
        private val hasInmates: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Jail %jail% still has inmates!")
                translation(MinecraftLocales.RU_RU, "Тюрьма %jail% содержит в себе заключенных!")
            }
        ),
        @SerialName("youVeBeenFreed")
        val released: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You have been released from jail!")
                translation(MinecraftLocales.RU_RU, "Вы были освобождены из тюрьмы!")
            }
        ),
        @SerialName("youVeBeenJailed")
        private val jailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You have been jailed for %time%!")
                translation(MinecraftLocales.RU_RU, "Вы были посажены в тюрьму на %time%!")
            }
        ),
        @SerialName("youInJail")
        val inJail: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You broke a rule, so you are in jail! Commands are unavailable!")
                translation(
                    MinecraftLocales.RU_RU,
                    "Вы что-то нарушили, поэтому находитесь в тюрьме! Команды недоступны!"
                )
            }
        ),
        @SerialName("jailedCommandBlocked")
        val commandBlocked: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Commands are unavailable while you are in jail!")
                translation(MinecraftLocales.RU_RU, "Команды недоступны, пока вы находитесь в тюрьме!")
            }
        ),
    ) {
        fun list(jails: String): LocalizableComponent = list.replace("%jails%", jails)
        fun created(name: String): LocalizableComponent = created.replace("%jail%", name)
        fun deleted(name: String): LocalizableComponent = deleted.replace("%jail%", name)
        fun inmateJailed(name: String, jail: String): LocalizableComponent = inmateJailed.replaceAll(
            PlaceholderReplacement.plain("%jail%", jail),
            PlaceholderReplacement.plain("%name%", name)
        )

        fun inmateReleased(name: String): LocalizableComponent = inmateReleased.replace("%name%", name)
        fun hasInmates(name: String): LocalizableComponent = hasInmates.replace("%jail%", name)
        fun jailed(time: String): LocalizableComponent = jailed.replace("%time%", time)
    }

    @Serializable
    data class Economy(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BECO&7] "),
        @SerialName("errorTransferMoney")
        val transferFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Could not give the currency")
                translation(MinecraftLocales.RU_RU, "&#db2c18Не удалось выдать валюту")
            }
        ),
        @SerialName("moneyTransferred")
        val transferred: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596The currency was given to the player")
                translation(MinecraftLocales.RU_RU, "&#42f596Валюта успешно выдана игроку")
            }
        ),
        @SerialName("playerBalance")
        private val balance: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596Player balance: %balance%")
                translation(MinecraftLocales.RU_RU, "&#42f596Баланс игрока %balance%")
            }
        ),
        @SerialName("currencies")
        private val currencies: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596Available currencies: %currencies%")
                translation(MinecraftLocales.RU_RU, "&#42f596Доступные валюты: %currencies%")
            }
        ),
        @SerialName("topsTitle")
        val topTitle: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596Top players by balance:")
                translation(MinecraftLocales.RU_RU, "&#42f596Топ игроков по балансу:")
            }
        ),
        @SerialName("topsEmpty")
        val topEmpty: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596The top is empty!")
                translation(MinecraftLocales.RU_RU, "&#42f596Топ игроков пуст!")
            }
        ),
        @SerialName("topItem")
        private val topItem: LocalizedText = prefix.concat(LocalizedText.shared("&#42f596%index%. %name% → %balance%")),
        @SerialName("currency_not_found")
        val currencyNotFound: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Currency not found!")
                translation(MinecraftLocales.RU_RU, "&#db2c18Валюта не найдена!")
            }
        ),
    ) {
        fun balance(amount: Number): LocalizableComponent = balance.replace(
            "%balance%",
            DecimalFormat("0.00").format(amount)
        )

        fun currencies(value: String): LocalizableComponent = currencies.replace("%currencies%", value)
        fun topItem(index: Int, name: String, balance: Number): LocalizableComponent = topItem.replaceAll(
            PlaceholderReplacement.plain("%index%", "$index"),
            PlaceholderReplacement.plain("%name%", name),
            PlaceholderReplacement.plain("%balance%", DecimalFormat("0.00").format(balance))
        )
    }

    /** Texts of `/menu` and of the menus it opens. */
    @Serializable
    data class Menu(
        @SerialName("prefix")
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BAspeKt&7] "),
        @SerialName("not_found")
        val notFound: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18No menu with this ID was found")
                translation(MinecraftLocales.RU_RU, "&#db2c18Меню с заданным ID не найдено")
            }
        ),
        @SerialName("not_enough_money")
        val notEnoughMoney: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Not enough money!")
                translation(MinecraftLocales.RU_RU, "&#db2c18Недостаточно средств!")
            }
        ),
    )

    @Serializable
    data class ChatGame(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BКВИЗ&7] "),
        @SerialName("solve_riddle")
        private val solveRiddle: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Riddle: %quiz% → &2/quiz ANSWER")
                translation(MinecraftLocales.RU_RU, "Загадка: %quiz% → &2/quiz ОТВЕТ")
            }
        ),
        @SerialName("solve_example")
        private val solveExample: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Solve: %quiz% → &2/quiz ANSWER")
                translation(MinecraftLocales.RU_RU, "Пример: %quiz% → &2/quiz ОТВЕТ")
            }
        ),
        @SerialName("solve_anagram")
        private val solveAnagram: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Anagram: %quiz% → &2/quiz ANSWER")
                translation(MinecraftLocales.RU_RU, "Анаграмма: %quiz% → &2/quiz ОТВЕТ")
            }
        ),
        @SerialName("solve_quadratic")
        private val solveQuadratic: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "Quadratic equation: %quiz% → &2/quiz ANSWER &7Any root, rounded to hundredths. For example: 0.02, 0.3, 1.0"
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "Квадратное уравнение: %quiz% → &2/quiz ОТВЕТ &7Любой вариант ответа с точностью до сотой. Например: 0.02, 0.3, 1.0"
                )
            }
        ),
        @SerialName("no_quiz_available")
        val noQuizAvailable: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18There is no active quiz right now!")
                translation(MinecraftLocales.RU_RU, "&#db2c18В данный момент нет активного квиза!")
            }
        ),
        @SerialName("wrong_answer")
        val wrongAnswer: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#db2c18Wrong answer!")
                translation(MinecraftLocales.RU_RU, "&#db2c18Ответ неверный!")
            }
        ),
        @SerialName("reward_not_paid")
        val rewardNotPaid: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "&#db2c18Your answer is right, but the reward could not be paid. Please tell an administrator."
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "&#db2c18Ответ верный, но награду не удалось выплатить. Сообщите администратору."
                )
            }
        ),
        @SerialName("game_ended")
        private val moneyRewarded: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "&6%player% &7guessed the right answer and received &6%amount% &7coins!"
                )
                translation(MinecraftLocales.RU_RU, "&6%player% &7угадал верный ответ! И получил &6%amount% &7монет!")
            }
        ),
    ) {
        fun solveRiddle(quiz: LocalizableComponent): LocalizableComponent = solveRiddle.replace("%quiz%", quiz)
        fun solveExample(quiz: String): LocalizableComponent = solveExample.replace("%quiz%", quiz)
        fun solveAnagram(quiz: String): LocalizableComponent = solveAnagram.replace("%quiz%", quiz)
        fun solveQuadratic(quiz: String): LocalizableComponent = solveQuadratic.replace("%quiz%", quiz)

        fun moneyRewarded(player: String, amount: Number): LocalizableComponent = moneyRewarded.replaceAll(
            PlaceholderReplacement.plain("%player%", player),
            PlaceholderReplacement.plain("%amount%", DecimalFormat("0.00").format(amount))
        )
    }

    @Serializable
    data class MoneyAdvancement(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BДОСТИЖЕНИЕ&7] "),
        @SerialName("challenge_completed")
        private val challengeCompleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You completed a challenge advancement and received %money% coins")
                translation(
                    MinecraftLocales.RU_RU,
                    "Вы выполнили достижение-челлендж и получили награду: %money% монет"
                )
            }
        ),
        @SerialName("goal_completed")
        private val goalCompleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You completed a goal advancement and received %money% coins")
                translation(MinecraftLocales.RU_RU, "Вы выполнили целевое достижение и получили награду: %money% монет")
            }
        ),
        @SerialName("task_completed")
        private val taskCompleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You completed an advancement and received %money% coins")
                translation(MinecraftLocales.RU_RU, "Вы выполнили достижение и получили награду: %money% монет")
            }
        ),
    ) {
        fun challengeCompleted(money: Number): LocalizableComponent = challengeCompleted.replace(
            "%money%",
            DecimalFormat("0.00").format(money)
        )

        fun goalCompleted(money: Number): LocalizableComponent = goalCompleted.replace(
            "%money%",
            DecimalFormat("0.00").format(money)
        )

        fun taskCompleted(money: Number): LocalizableComponent = taskCompleted.replace(
            "%money%",
            DecimalFormat("0.00").format(money)
        )
    }

    @Serializable
    data class MoneyDrop(
        @SerialName("prefix")
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BAspeKt&7] "),
        @SerialName("item_name")
        val itemName: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&6Coin")
            translation(MinecraftLocales.RU_RU, "&6Монетка")
        },
        @SerialName("picked_up")
        private val pickedUp: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#42f596You picked up {AMOUNT} mysterious coins")
                translation(MinecraftLocales.RU_RU, "&#42f596Вы подобрали {AMOUNT} загадочных монет")
            }
        ),
    ) {
        fun pickedUp(amount: Number): LocalizableComponent {
            return pickedUp.replace("{AMOUNT}", DecimalFormat("0.00").format(amount))
        }
    }

    @Serializable
    data class Claim(
        @SerialName("prefix")
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BCLAIM&7] "),
        @SerialName("flag_changed")
        val flagChanged: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Chunk flag changed!")
                translation(MinecraftLocales.RU_RU, "Флаг чанка изменен!")
            }
        ),
        @SerialName("claimed")
        val claimed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You claimed the chunk!")
                translation(MinecraftLocales.RU_RU, "Вы заняли чанк!")
            }
        ),
        @SerialName("unclaimed")
        val unclaimed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "The chunk is free!")
                translation(MinecraftLocales.RU_RU, "Чанк свободен!")
            }
        ),
        @SerialName("error")
        val error: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Error! See the console")
                translation(MinecraftLocales.RU_RU, "Ошибка! Смотрите консоль")
            }
        ),
        @SerialName("map")
        val mapTitle: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Chunk map:")
                translation(MinecraftLocales.RU_RU, "Карта блоков:")
            }
        ),
        @SerialName("member_added")
        val memberAdded: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Member added")
                translation(MinecraftLocales.RU_RU, "Участник добавлен")
            }
        ),
        @SerialName("member_removed")
        val memberRemoved: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Member removed")
                translation(MinecraftLocales.RU_RU, "Участник удален")
            }
        ),
        @SerialName("action_blocked")
        private val actionBlocked: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Error! %action% is blocked in this chunk!")
                translation(MinecraftLocales.RU_RU, "Ошибка! Действие %action% заблокировано на этом чанке!")
            }
        ),
        @SerialName("chunk_under_claim")
        val alreadyClaimed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "This chunk is already claimed!")
                translation(MinecraftLocales.RU_RU, "Этот чанк уже запривачен!")
            }
        ),
        @SerialName("no_claim_here")
        val noneHere: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "There is no claim here!")
                translation(MinecraftLocales.RU_RU, "На этом месте нет привата!")
            }
        ),
        @SerialName("not_claim_owner")
        val notOwner: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "You are not the owner of this claim!")
                translation(MinecraftLocales.RU_RU, "Вы не владелец привата!")
            }
        ),
        @SerialName("already_member")
        val alreadyMember: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "The player is already a member of the claim!")
                translation(MinecraftLocales.RU_RU, "Игрок уже участник привата!")
            }
        ),
        @SerialName("not_member")
        val notMember: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "The player is not a member of the claim!")
                translation(MinecraftLocales.RU_RU, "Игрок не участник привата!")
            }
        ),
    ) {
        fun actionBlocked(action: String): LocalizableComponent = actionBlocked.replace("%action%", action)
    }

    @Serializable
    data class Sit(
        @SerialName("prefix")
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BSIT&7] "),
        @SerialName("already")
        val already: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#dbbb18You are already sitting")
                translation(MinecraftLocales.RU_RU, "&#dbbb18Вы уже сидите")
            }
        ),
        @SerialName("air")
        val inAir: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#dbbb18You cannot sit in the air")
                translation(MinecraftLocales.RU_RU, "&#dbbb18Нельзя сидеть в воздухе")
            }
        ),
        @SerialName("too_far")
        val tooFar: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#dbbb18Too far away")
                translation(MinecraftLocales.RU_RU, "&#dbbb18Слишком далеко")
            }
        ),
        @SerialName("cant_sit_in_block")
        val insideBlock: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "&#dbbb18You cannot sit inside a block")
                translation(MinecraftLocales.RU_RU, "&#dbbb18Нельзя сидеть в блоке")
            }
        )
    )

    @Serializable
    data class NewBee(
        @SerialName("prefix")
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BЗАЩИТА&7] "),
        @SerialName("youAreNewBee")
        val welcome: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "&#1D72F2You are a newcomer! &6So the game is easier for you during the first 50 minutes! Enjoy!"
                )
                translation(
                    MinecraftLocales.RU_RU,
                    "&#1D72F2Вы новичок! &6Поэтому в первые 50 минут вам будет играть легче! Наслаждайтесь игрой!"
                )
            }
        ),
        @SerialName("newBeeTitle")
        val title: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&#DBB72BNewcomer protection")
            translation(MinecraftLocales.RU_RU, "&#DBB72BЗащита новичка")
        },
        @SerialName("newBeeSubtitle")
        val subtitle: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.EN_US, "&#db2c18Enabled")
            translation(MinecraftLocales.RU_RU, "&#db2c18Включена")
        },
        @SerialName("newBeeShieldForceDisabled")
        val protectionRemoved: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.EN_US,
                    "You started a fight with a player. Your newcomer protection has been removed"
                )
                translation(MinecraftLocales.RU_RU, "Вы вступили в бой с игроком. Защита новичка была удалена")
            }
        )
    )

    @Serializable
    data class Swear(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BSF&7] "),
        @SerialName("swearFilterEnabled")
        val enabled: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Swear filter enabled")
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов включен")
            }
        ),
        @SerialName("swearFilterDisabled")
        val disabled: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Swear filter disabled")
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов выключен")
            }
        ),
        @SerialName("swearFilterEnabledFor")
        private val enabledFor: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Swear filter enabled for {player}")
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов включен для {player}")
            }
        ),
        @SerialName("swearFilterDisabledFor")
        private val disabledFor: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.EN_US, "Swear filter disabled for {player}")
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов выключен для {player}")
            }
        ),
    ) {
        fun enabledFor(name: String): LocalizableComponent = enabledFor.replace("{player}", name)
        fun disabledFor(name: String): LocalizableComponent = disabledFor.replace("{player}", name)
    }
}
