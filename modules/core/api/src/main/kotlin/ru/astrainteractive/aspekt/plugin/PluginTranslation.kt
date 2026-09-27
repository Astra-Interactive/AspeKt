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
                translation(MinecraftLocales.RU_RU, "&#db2c18У вас нет прав!")
                translation(MinecraftLocales.EN_US, "&#db2c18You don't have permission!")
            }
        ),
        @SerialName("wrong_usage")
        val wrongUsage: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#db2c18Неверное использование!")
                translation(MinecraftLocales.EN_US, "&#db2c18Wrong usage!")
            }
        ),
        @SerialName("only_player_command")
        val onlyPlayerCommand: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#db2c18Эта команда только для игроков!")
                translation(MinecraftLocales.EN_US, "&#db2c18This command is for players only!")
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
                translation(MinecraftLocales.RU_RU, "&#dbbb18Перезагрузка плагина")
                translation(MinecraftLocales.EN_US, "&#dbbb18Reloading the plugin")
            }
        ),
        @SerialName("completed")
        val completed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Перезагрузка успешно завершена")
                translation(MinecraftLocales.EN_US, "&#42f596Reload complete")
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
                    MinecraftLocales.RU_RU,
                    "&#42f596Вы получили &6%amount% &#42f596монет за время, проведённое на сервере!"
                )
                translation(
                    MinecraftLocales.EN_US,
                    "&#42f596You received &6%amount% &#42f596coins for the time spent on the server!"
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
                translation(MinecraftLocales.RU_RU, "Достигнуто максимальное количество одновременных телепортов!")
                translation(MinecraftLocales.EN_US, "Too many teleports are running at once!")
            }
        ),
        @SerialName("timeout")
        val timeout: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Подождите немного прежде чем телепортироваться!")
                translation(MinecraftLocales.EN_US, "Wait a little before teleporting again!")
            }
        ),
        @SerialName("found_place")
        val foundPlace: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Найдено место для вас!")
                translation(MinecraftLocales.EN_US, "Found a place for you!")
            }
        ),
        @SerialName("not_found_place")
        val notFoundPlace: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Не удалось найти место!")
                translation(MinecraftLocales.EN_US, "Could not find a place!")
            }
        ),
        @SerialName("max_rtp_retries")
        val maxRetries: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Не удалось найти безопасное место за отведённое число попыток!")
                translation(
                    MinecraftLocales.EN_US,
                    "Could not find a safe place within the allowed number of attempts!"
                )
            }
        ),
        @SerialName("searching")
        val searching: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Ищем для вас место...")
                translation(MinecraftLocales.EN_US, "Looking for a place for you...")
            }
        ),
        @SerialName("low_tick_time")
        private val lowTickTime: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Слишком маленький TPS: %tps%!")
                translation(MinecraftLocales.EN_US, "TPS is too low: %tps%!")
            }
        ),
        @SerialName("did_you_mean_tpr")
        val didYouMeanTpr: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#db2c18Возможно, вы хотели ввести /tpr")
                translation(MinecraftLocales.EN_US, "&#db2c18Maybe you meant /tpr")
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
                translation(MinecraftLocales.RU_RU, "Вы не ожидаете телепортации к игроку")
                translation(MinecraftLocales.EN_US, "You are not waiting to teleport to anyone")
            }
        ),
        @SerialName("request_cancelled")
        val requestCancelled: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Запрос отменен")
                translation(MinecraftLocales.EN_US, "Request cancelled")
            }
        ),
        @SerialName("no_pending_tp_to_deny")
        val noPendingTpToDeny: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "У вас нет активных запросов")
                translation(MinecraftLocales.EN_US, "You have no active requests")
            }
        ),
        @SerialName("cant_tp_self")
        val cantTpSelf: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Нельзя отправить запрос самому себе")
                translation(MinecraftLocales.EN_US, "You cannot send a request to yourself")
            }
        ),
        @SerialName("request_sent")
        val requestSent: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Запрос отправлен")
                translation(MinecraftLocales.EN_US, "Request sent")
            }
        ),
        @SerialName("request_accepted")
        val requestAccepted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Запрос принят")
                translation(MinecraftLocales.EN_US, "Request accepted")
            }
        ),
        @SerialName("request_denied")
        private val requestDenied: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Игрок %player% отменил запрос")
                translation(MinecraftLocales.EN_US, "%player% denied the request")
            }
        ),
        @SerialName("request_tpa")
        private val requestTpa: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Игрок %player% хочет телепортировать вас к себе")
                translation(MinecraftLocales.EN_US, "%player% wants to teleport you to them")
            }
        ),
        @SerialName("request_tpa_here")
        private val requestTpaHere: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Игрок %player% хочет к вам телепортироваться")
                translation(MinecraftLocales.EN_US, "%player% wants to teleport to you")
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
                translation(MinecraftLocales.RU_RU, "Дом создан!")
                translation(MinecraftLocales.EN_US, "Home created!")
            }
        ),
        @SerialName("homeOverridden")
        val overwritten: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Дом перезаписан!")
                translation(MinecraftLocales.EN_US, "Home overwritten!")
            }
        ),
        @SerialName("homeAlreadyExists")
        val alreadyExists: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "Дом с таким именем уже существует! Добавьте force в конец команды, чтобы перезаписать его"
                )
                translation(
                    MinecraftLocales.EN_US,
                    "A home with this name already exists! Add force to the end of the command to overwrite it"
                )
            }
        ),
        @SerialName("homeLimitReached")
        private val limitReached: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы достигли лимита домов: %limit%")
                translation(MinecraftLocales.EN_US, "You have reached the home limit: %limit%")
            }
        ),
        @SerialName("homeNotFound")
        val notFound: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Такой дом не найден!")
                translation(MinecraftLocales.EN_US, "Home not found!")
            }
        ),
        @SerialName("homeDeleted")
        val deleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Дом удален!")
                translation(MinecraftLocales.EN_US, "Home deleted!")
            }
        ),
        @SerialName("teleporting")
        val teleported: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы были телепортированы домой")
                translation(MinecraftLocales.EN_US, "You have been teleported home")
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
                translation(MinecraftLocales.RU_RU, "Список: %jails%")
                translation(MinecraftLocales.EN_US, "Jails: %jails%")
            }
        ),
        @SerialName("jailCreatedSuccess")
        private val created: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Тюрьма создана: %jail%")
                translation(MinecraftLocales.EN_US, "Jail created: %jail%")
            }
        ),
        @SerialName("jailCreatedFail")
        val creationFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Не удалось создать тюрьму. Смотрите консоль для подробностей.")
                translation(MinecraftLocales.EN_US, "Could not create the jail. See the console for details.")
            }
        ),
        @SerialName("jailDeleteSuccess")
        private val deleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Тюрьма удалена: %jail%")
                translation(MinecraftLocales.EN_US, "Jail deleted: %jail%")
            }
        ),
        @SerialName("jailDeleteFail")
        val deletionFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Не удалось удалить тюрьму. Смотрите консоль для подробностей.")
                translation(MinecraftLocales.EN_US, "Could not delete the jail. See the console for details.")
            }
        ),
        @SerialName("inmateAddSuccess")
        private val inmateJailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Заключенный %name% посажен в %jail%")
                translation(MinecraftLocales.EN_US, "%name% has been jailed in %jail%")
            }
        ),
        @SerialName("inmateAddFail")
        val jailingFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Не удалось посадить в тюрьму. Смотрите консоль для подробностей.")
                translation(MinecraftLocales.EN_US, "Could not jail the player. See the console for details.")
            }
        ),
        @SerialName("inmateFreeSuccess")
        private val inmateReleased: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Заключенный %name% освобожден")
                translation(MinecraftLocales.EN_US, "%name% has been released")
            }
        ),
        @SerialName("inmateFreeFail")
        val releaseFailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "Не удалось освободить из тюрьмы. Смотрите консоль для подробностей."
                )
                translation(
                    MinecraftLocales.EN_US,
                    "Could not release the player from jail. See the console for details."
                )
            }
        ),
        @SerialName("jailHasInmates")
        private val hasInmates: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Тюрьма %jail% содержит в себе заключенных!")
                translation(MinecraftLocales.EN_US, "Jail %jail% still has inmates!")
            }
        ),
        @SerialName("youVeBeenFreed")
        val released: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы были освобождены из тюрьмы!")
                translation(MinecraftLocales.EN_US, "You have been released from jail!")
            }
        ),
        @SerialName("youVeBeenJailed")
        private val jailed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы были посажены в тюрьму на %time%!")
                translation(MinecraftLocales.EN_US, "You have been jailed for %time%!")
            }
        ),
        @SerialName("youInJail")
        val inJail: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "Вы что-то нарушили, поэтому находитесь в тюрьме! Команды недоступны!"
                )
                translation(MinecraftLocales.EN_US, "You broke a rule, so you are in jail! Commands are unavailable!")
            }
        ),
        @SerialName("jailedCommandBlocked")
        val commandBlocked: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Команды недоступны, пока вы находитесь в тюрьме!")
                translation(MinecraftLocales.EN_US, "Commands are unavailable while you are in jail!")
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
                translation(MinecraftLocales.RU_RU, "&#db2c18Не удалось выдать валюту")
                translation(MinecraftLocales.EN_US, "&#db2c18Could not give the currency")
            }
        ),
        @SerialName("moneyTransferred")
        val transferred: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Валюта успешно выдана игроку")
                translation(MinecraftLocales.EN_US, "&#42f596The currency was given to the player")
            }
        ),
        @SerialName("playerBalance")
        private val balance: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Баланс игрока %balance%")
                translation(MinecraftLocales.EN_US, "&#42f596Player balance: %balance%")
            }
        ),
        @SerialName("currencies")
        private val currencies: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Доступные валюты: %currencies%")
                translation(MinecraftLocales.EN_US, "&#42f596Available currencies: %currencies%")
            }
        ),
        @SerialName("topsTitle")
        val topTitle: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Топ игроков по балансу:")
                translation(MinecraftLocales.EN_US, "&#42f596Top players by balance:")
            }
        ),
        @SerialName("topsEmpty")
        val topEmpty: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Топ игроков пуст!")
                translation(MinecraftLocales.EN_US, "&#42f596The top is empty!")
            }
        ),
        @SerialName("topItem")
        private val topItem: LocalizedText = prefix.concat(LocalizedText.shared("&#42f596%index%. %name% → %balance%")),
        @SerialName("currency_not_found")
        val currencyNotFound: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#db2c18Валюта не найдена!")
                translation(MinecraftLocales.EN_US, "&#db2c18Currency not found!")
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
                translation(MinecraftLocales.RU_RU, "&#db2c18Меню с заданным ID не найдено")
                translation(MinecraftLocales.EN_US, "&#db2c18No menu with this ID was found")
            }
        ),
        @SerialName("not_enough_money")
        val notEnoughMoney: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#db2c18Недостаточно средств!")
                translation(MinecraftLocales.EN_US, "&#db2c18Not enough money!")
            }
        ),
    )

    @Serializable
    data class ChatGame(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BКВИЗ&7] "),
        @SerialName("solve_riddle")
        private val solveRiddle: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Загадка: %quiz% → &2/quiz ОТВЕТ")
                translation(MinecraftLocales.EN_US, "Riddle: %quiz% → &2/quiz ANSWER")
            }
        ),
        @SerialName("solve_example")
        private val solveExample: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Пример: %quiz% → &2/quiz ОТВЕТ")
                translation(MinecraftLocales.EN_US, "Solve: %quiz% → &2/quiz ANSWER")
            }
        ),
        @SerialName("solve_anagram")
        private val solveAnagram: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Анаграмма: %quiz% → &2/quiz ОТВЕТ")
                translation(MinecraftLocales.EN_US, "Anagram: %quiz% → &2/quiz ANSWER")
            }
        ),
        @SerialName("solve_quadratic")
        private val solveQuadratic: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(
                    MinecraftLocales.RU_RU,
                    "Квадратное уравнение: %quiz% → &2/quiz ОТВЕТ &7Любой вариант ответа с точностью до сотой. Например: 0.02, 0.3, 1.0"
                )
                translation(
                    MinecraftLocales.EN_US,
                    "Quadratic equation: %quiz% → &2/quiz ANSWER &7Any root, rounded to hundredths. For example: 0.02, 0.3, 1.0"
                )
            }
        ),
        @SerialName("no_quiz_available")
        val noQuizAvailable: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#db2c18В данный момент нет активного квиза!")
                translation(MinecraftLocales.EN_US, "&#db2c18There is no active quiz right now!")
            }
        ),
        @SerialName("wrong_answer")
        val wrongAnswer: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#db2c18Ответ неверный!")
                translation(MinecraftLocales.EN_US, "&#db2c18Wrong answer!")
            }
        ),
        @SerialName("game_ended")
        private val moneyRewarded: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&6%player% &7угадал верный ответ! И получил &6%amount% &7монет!")
                translation(
                    MinecraftLocales.EN_US,
                    "&6%player% &7guessed the right answer and received &6%amount% &7coins!"
                )
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
                translation(
                    MinecraftLocales.RU_RU,
                    "Вы выполнили достижение-челлендж и получили награду: %money% монет"
                )
                translation(MinecraftLocales.EN_US, "You completed a challenge advancement and received %money% coins")
            }
        ),
        @SerialName("goal_completed")
        private val goalCompleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы выполнили целевое достижение и получили награду: %money% монет")
                translation(MinecraftLocales.EN_US, "You completed a goal advancement and received %money% coins")
            }
        ),
        @SerialName("task_completed")
        private val taskCompleted: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы выполнили достижение и получили награду: %money% монет")
                translation(MinecraftLocales.EN_US, "You completed an advancement and received %money% coins")
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
            translation(MinecraftLocales.RU_RU, "&6Монетка")
            translation(MinecraftLocales.EN_US, "&6Coin")
        },
        @SerialName("picked_up")
        private val pickedUp: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#42f596Вы подобрали {AMOUNT} загадочных монет")
                translation(MinecraftLocales.EN_US, "&#42f596You picked up {AMOUNT} mysterious coins")
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
                translation(MinecraftLocales.RU_RU, "Флаг чанка изменен!")
                translation(MinecraftLocales.EN_US, "Chunk flag changed!")
            }
        ),
        @SerialName("claimed")
        val claimed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы заняли чанк!")
                translation(MinecraftLocales.EN_US, "You claimed the chunk!")
            }
        ),
        @SerialName("unclaimed")
        val unclaimed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Чанк свободен!")
                translation(MinecraftLocales.EN_US, "The chunk is free!")
            }
        ),
        @SerialName("error")
        val error: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Ошибка! Смотрите консоль")
                translation(MinecraftLocales.EN_US, "Error! See the console")
            }
        ),
        @SerialName("map")
        val mapTitle: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Карта блоков:")
                translation(MinecraftLocales.EN_US, "Chunk map:")
            }
        ),
        @SerialName("member_added")
        val memberAdded: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Участник добавлен")
                translation(MinecraftLocales.EN_US, "Member added")
            }
        ),
        @SerialName("member_removed")
        val memberRemoved: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Участник удален")
                translation(MinecraftLocales.EN_US, "Member removed")
            }
        ),
        @SerialName("action_blocked")
        private val actionBlocked: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Ошибка! Действие %action% заблокировано на этом чанке!")
                translation(MinecraftLocales.EN_US, "Error! %action% is blocked in this chunk!")
            }
        ),
        @SerialName("chunk_under_claim")
        val alreadyClaimed: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Этот чанк уже запривачен!")
                translation(MinecraftLocales.EN_US, "This chunk is already claimed!")
            }
        ),
        @SerialName("no_claim_here")
        val noneHere: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "На этом месте нет привата!")
                translation(MinecraftLocales.EN_US, "There is no claim here!")
            }
        ),
        @SerialName("not_claim_owner")
        val notOwner: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы не владелец привата!")
                translation(MinecraftLocales.EN_US, "You are not the owner of this claim!")
            }
        ),
        @SerialName("already_member")
        val alreadyMember: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Игрок уже участник привата!")
                translation(MinecraftLocales.EN_US, "The player is already a member of the claim!")
            }
        ),
        @SerialName("not_member")
        val notMember: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Игрок не участник привата!")
                translation(MinecraftLocales.EN_US, "The player is not a member of the claim!")
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
                translation(MinecraftLocales.RU_RU, "&#dbbb18Вы уже сидите")
                translation(MinecraftLocales.EN_US, "&#dbbb18You are already sitting")
            }
        ),
        @SerialName("air")
        val inAir: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#dbbb18Нельзя сидеть в воздухе")
                translation(MinecraftLocales.EN_US, "&#dbbb18You cannot sit in the air")
            }
        ),
        @SerialName("too_far")
        val tooFar: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#dbbb18Слишком далеко")
                translation(MinecraftLocales.EN_US, "&#dbbb18Too far away")
            }
        ),
        @SerialName("cant_sit_in_block")
        val insideBlock: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "&#dbbb18Нельзя сидеть в блоке")
                translation(MinecraftLocales.EN_US, "&#dbbb18You cannot sit inside a block")
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
                    MinecraftLocales.RU_RU,
                    "&#1D72F2Вы новичок! &6Поэтому в первые 50 минут вам будет играть легче! Наслаждайтесь игрой!"
                )
                translation(
                    MinecraftLocales.EN_US,
                    "&#1D72F2You are a newcomer! &6So the game is easier for you during the first 50 minutes! Enjoy!"
                )
            }
        ),
        @SerialName("newBeeTitle")
        val title: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&#DBB72BЗащита новичка")
            translation(MinecraftLocales.EN_US, "&#DBB72BNewcomer protection")
        },
        @SerialName("newBeeSubtitle")
        val subtitle: LocalizedText = LocalizedText.build {
            translation(MinecraftLocales.RU_RU, "&#db2c18Включена")
            translation(MinecraftLocales.EN_US, "&#db2c18Enabled")
        },
        @SerialName("newBeeShieldForceDisabled")
        val protectionRemoved: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Вы вступили в бой с игроком. Защита новичка была удалена")
                translation(
                    MinecraftLocales.EN_US,
                    "You started a fight with a player. Your newcomer protection has been removed"
                )
            }
        )
    )

    @Serializable
    data class Swear(
        val prefix: LocalizedText = LocalizedText.shared("&7[&#DBB72BSF&7] "),
        @SerialName("swearFilterEnabled")
        val enabled: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов включен")
                translation(MinecraftLocales.EN_US, "Swear filter enabled")
            }
        ),
        @SerialName("swearFilterDisabled")
        val disabled: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов выключен")
                translation(MinecraftLocales.EN_US, "Swear filter disabled")
            }
        ),
        @SerialName("swearFilterEnabledFor")
        private val enabledFor: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов включен для {player}")
                translation(MinecraftLocales.EN_US, "Swear filter enabled for {player}")
            }
        ),
        @SerialName("swearFilterDisabledFor")
        private val disabledFor: LocalizedText = prefix.concat(
            LocalizedText.build {
                translation(MinecraftLocales.RU_RU, "Фильтр плохих слов выключен для {player}")
                translation(MinecraftLocales.EN_US, "Swear filter disabled for {player}")
            }
        ),
    ) {
        fun enabledFor(name: String): LocalizableComponent = enabledFor.replace("{player}", name)
        fun disabledFor(name: String): LocalizableComponent = disabledFor.replace("{player}", name)
    }
}
