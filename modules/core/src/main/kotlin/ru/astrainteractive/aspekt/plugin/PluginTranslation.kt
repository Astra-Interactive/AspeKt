@file:Suppress("MaxLineLength", "MaximumLineLength", "LongParameterList")

package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.StringDescExt.plus
import ru.astrainteractive.astralibs.string.StringDescExt.replace
import java.text.DecimalFormat

/**
 * All translation stored here
 */
@Serializable
class PluginTranslation(
    @SerialName("general")
    val general: General = General(),
    @SerialName("sit")
    val sit: Sit = Sit(),
    @SerialName("claim")
    val claim: Claim = Claim(),
    @SerialName("money_advancement")
    val moneyAdvancement: MoneyAdvancement = MoneyAdvancement(),
    @SerialName("newbee")
    val newBee: NewBee = NewBee(),
    @SerialName("swear")
    val swear: Swear = Swear(),
    @SerialName("chat_game")
    val chatGame: ChatGame = ChatGame(),
    @SerialName("economy")
    val economy: Economy = Economy(),
    @SerialName("jails")
    val jails: Jails = Jails(),
    @SerialName("homes")
    val homes: Homes = Homes(),
) {
    @Serializable
    data class Homes(
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BHOMES&7] "),
        val homeCreated: StringDesc.Raw = prefix
            .plus("Дом создан!")
            .toRaw(),
        val homeNotFound: StringDesc.Raw = prefix
            .plus("Такой дом не найден!")
            .toRaw(),
        val homeDeleted: StringDesc.Raw = prefix
            .plus("Дом удален!")
            .toRaw(),
        val teleporting: StringDesc.Raw = prefix
            .plus("Вы были телепортированы домой")
            .toRaw(),
    )

    @Serializable
    data class Jails(
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BJAIL&7] "),
        private val jailsList: StringDesc.Raw = prefix
            .plus("Список: %jails%")
            .toRaw(),
        private val jailCreatedSuccess: StringDesc.Raw = prefix
            .plus("Тюрьма создана: %jail%")
            .toRaw(),
        val jailCreatedFail: StringDesc.Raw = prefix
            .plus("Не удалось создать тюрьму. Смотрите консоль для подробностей.")
            .toRaw(),
        private val jailDeleteSuccess: StringDesc.Raw = prefix
            .plus("Тюрьма удалена: %jail%")
            .toRaw(),
        val jailDeleteFail: StringDesc.Raw = prefix
            .plus("Не удалось удалить тюрьму. Смотрите консоль для подробностей.")
            .toRaw(),
        private val inmateAddSuccess: StringDesc.Raw = prefix
            .plus("Заключенный %name% посажен в %jail%")
            .toRaw(),
        val inmateAddFail: StringDesc.Raw = prefix
            .plus("Не удалось посадить в тюрьму. Смотрите консоль для подробностей.")
            .toRaw(),
        private val inmateFreeSuccess: StringDesc.Raw = prefix
            .plus("Заключенный %name% освобожден")
            .toRaw(),
        val inmateFreeFail: StringDesc.Raw = prefix
            .plus("Не удалось освободить bp тюрьмы. Смотрите консоль для подробностей.")
            .toRaw(),
        private val jailHasInmates: StringDesc.Raw = prefix
            .plus("Тюрьма %jail% содержит в себе заключенных!")
            .toRaw(),
        val youVeBeenFreed: StringDesc.Raw = prefix
            .plus("Вы были освобождены из тюрьмы!")
            .toRaw(),
        private val youVeBeenJailed: StringDesc.Raw = prefix
            .plus("Вы были посажены в тюрьму на %time%!")
            .toRaw(),
        val youInJail: StringDesc.Raw = prefix
            .plus("Вы что-то нарушили, поэтому находитесь в тюрьме! Команды недоступны!")
            .toRaw(),
        val jailedCommandBlocked: StringDesc.Raw = prefix
            .plus("Команды недоступны, пока вы находитесь в тюрьме!")
            .toRaw(),
    ) {
        fun jailsList(jails: String) = jailsList.replace("%jails%", jails)
        fun jailCreatedSuccess(name: String) = jailCreatedSuccess.replace("%jail%", name)
        fun jailDeleteSuccess(name: String) = jailDeleteSuccess.replace("%jail%", name)
        fun inmateAddSuccess(name: String, jail: String) = inmateAddSuccess
            .replace("%jail%", jail)
            .replace("%name%", name)

        fun inmateFreeSuccess(name: String) = inmateFreeSuccess.replace("%name%", name)
        fun jailHasInmates(name: String) = jailHasInmates.replace("%jail%", name)
        fun youVeBeenJailed(time: String) = youVeBeenJailed.replace("%time%", time)
    }

    @Serializable
    class Economy(
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BECO&7]"),
        val playerNotFound: StringDesc.Raw = prefix
            .plus("&#db2c18Игрок не найден")
            .toRaw(),
        val errorTransferMoney: StringDesc.Raw = prefix
            .plus("&#db2c18Не удалось выдать валюту")
            .toRaw(),
        val moneyTransferred: StringDesc.Raw = prefix
            .plus("&#42f596Валюта успешно выдана игроку")
            .toRaw(),
        private val playerBalance: StringDesc.Raw = prefix
            .plus("&#42f596Баланс игрока %balance%")
            .toRaw(),
        private val currencies: StringDesc.Raw = prefix
            .plus("&#42f596Доступные валюту: %currencies%")
            .toRaw(),
        val topsTitle: StringDesc.Raw = prefix
            .plus("&#42f596Топ игроков по балансу:")
            .toRaw(),
        val topsEmpty: StringDesc.Raw = prefix
            .plus("&#42f596Топ игроков пуст!")
            .toRaw(),
        private val topItem: StringDesc.Raw = prefix
            .plus("&#42f596%index%. %name% → %balance%")
            .toRaw(),

        @SerialName("currency_not_found")
        val currencyNotFound: StringDesc.Raw = prefix
            .plus("&#db2c18Валюта не найдена!")
            .toRaw(),
    ) {
        fun playerBalance(amount: Number) = playerBalance.replace("%balance%", DecimalFormat("0.00").format(amount))
        fun currencies(value: String) = currencies.replace("%currencies%", value)
        fun topItem(index: Int, name: String, balance: Number) = topItem
            .replace("%index%", "$index")
            .replace("%name%", "$name")
            .replace("%balance%", DecimalFormat("0.00").format(balance))
    }

    @Serializable
    class ChatGame(
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BКВИЗ&7] "),
        @SerialName("solve_riddle")
        private val solveRiddle: StringDesc.Raw = prefix
            .plus("Загадка: %quiz% → &2/quiz ОТВЕТ")
            .toRaw(),
        @SerialName("solve_example")
        private val solveExample: StringDesc.Raw = prefix
            .plus("Пример: %quiz% → &2/quiz ОТВЕТ")
            .toRaw(),
        @SerialName("solve_anagram")
        private val solveAnagram: StringDesc.Raw = prefix
            .plus("Анаграмма: %quiz% → &2/quiz ОТВЕТ")
            .toRaw(),
        @SerialName("solve_quadratic")
        private val solveQuadratic: StringDesc.Raw = prefix
            .plus(
                "Квадратное уравнение: %quiz% → &2/quiz ОТВЕТ &7Любой вариант ответа с точностью до сотой. Например: 0.02, 0.3, 1.0"
            )
            .toRaw(),
        @SerialName("no_quiz_available")
        val noQuizAvailable: StringDesc.Raw = prefix
            .plus("&#db2c18В данный момент нет активного квиза!")
            .toRaw(),
        @SerialName("wrong_answer")
        val wrongAnswer: StringDesc.Raw = prefix
            .plus("&#db2c18Ответ неверный!")
            .toRaw(),
        @SerialName("game_ended")
        val gameEndedMoneyReward: StringDesc.Raw = prefix
            .plus("&6%player% &7угадал верный ответ! И получил &6%amount% &7монет!")
            .toRaw(),
    ) {
        fun solveRiddle(quiz: String) = solveRiddle.replace("%quiz%", quiz)
        fun solveExample(quiz: String) = solveExample.replace("%quiz%", quiz)
        fun solveAnagram(quiz: String) = solveAnagram.replace("%quiz%", quiz)
        fun solveQuadratic(quiz: String) = solveQuadratic.replace("%quiz%", quiz)

        fun gameEndedMoneyReward(player: String, amount: Number) = gameEndedMoneyReward
            .replace("%player%", player)
            .replace("%amount%", DecimalFormat("0.00").format(amount))
    }

    @Serializable
    class MoneyAdvancement(
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BДОСТИЖЕНИЕ&7] "),
        @SerialName("reload_complete")
        private val challengeCompleted: StringDesc.Raw = prefix
            .plus("Вы выполднили достижение-челлендж и получили награду: %money% монет")
            .toRaw(),
        @SerialName("goal_completed")
        private val goalCompleted: StringDesc.Raw = prefix
            .plus("Вы выполднили целевое достижение и получили награду: %money% монет")
            .toRaw(),
        @SerialName("task_completed")
        private val taskCompleted: StringDesc.Raw = prefix
            .plus("Вы выполднили достижение и получили награду: %money% монет")
            .toRaw(),
    ) {
        fun challengeCompleted(money: Number) = challengeCompleted.replace(
            "%money%",
            DecimalFormat("0.00").format(money)
        )

        fun goalCompleted(money: Number) = goalCompleted.replace("%money%", DecimalFormat("0.00").format(money))
        fun taskCompleted(money: Number) = taskCompleted.replace("%money%", DecimalFormat("0.00").format(money))
    }

    @Serializable
    class General(
        @SerialName("prefix")
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BAspeKt&7] "),
        @SerialName("reload")
        val reload: StringDesc.Raw = prefix
            .plus("&#dbbb18Перезагрузка плагина")
            .toRaw(),
        @SerialName("reload_complete")
        val reloadComplete: StringDesc.Raw = prefix
            .plus("&#42f596Перезагрузка успешно завершена")
            .toRaw(),
        @SerialName("no_permission")
        val noPermission: StringDesc.Raw = prefix
            .plus("&#db2c18У вас нет прав!")
            .toRaw(),
        @SerialName("not_enough_money")
        val notEnoughMoney: StringDesc.Raw = prefix
            .plus("&#db2c18Недостаточно средств!")
            .toRaw(),
        @SerialName("wrong_usage")
        val wrongUsage: StringDesc.Raw = prefix
            .plus("&#db2c18Неверное использование!")
            .toRaw(),
        @SerialName("only_player_command")
        val onlyPlayerCommand: StringDesc.Raw = prefix
            .plus("&#db2c18Эта команда только для игроков!")
            .toRaw(),
        @SerialName("menu_not_found")
        val menuNotFound: StringDesc.Raw = prefix
            .plus("&#db2c18Меню с заданным ID не найдено")
            .toRaw(),
        @SerialName("discord_link_reward")
        private val discordLinkReward: StringDesc.Raw = prefix
            .plus("&#42f596Вы получили {AMOUNT}$ за привязку дискорда!")
            .toRaw(),
        @SerialName("picked_up_money")
        private val pickedUpMoney: StringDesc.Raw = prefix
            .plus("&#42f596Вы подобрали {AMOUNT} загадочных монет")
            .toRaw(),
        @SerialName("dropped_money")
        val droppedMoney: StringDesc.Raw = StringDesc.Raw("&6Монетка"),
        @SerialName("maybe_tpr")
        val maybeTpr: StringDesc.Raw = prefix
            .plus("&#db2c18Возможно, вы хотели ввести /tpr")
            .toRaw(),
        private val commandError: StringDesc.Raw = prefix
            .plus("&#db2c18Ошибка выполнения команды: %error%")
            .toRaw()
    ) {

        fun commandError(error: String): StringDesc = commandError.replace("%error%", error)

        fun discordLinkReward(amount: Number): StringDesc {
            return discordLinkReward.replace("{AMOUNT}", DecimalFormat("0.00").format(amount))
        }

        fun pickedUpMoney(amount: Number): StringDesc {
            return pickedUpMoney
                .replace("{AMOUNT}", DecimalFormat("0.00").format(amount))
        }
    }

    @Serializable
    class Claim(
        @SerialName("prefix")
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BCLAIM&7] "),
        // Admin claim
        @SerialName("flag_changed")
        val chunkFlagChanged: StringDesc.Raw = prefix
            .plus("Флаг чанка изменен!")
            .toRaw(),
        @SerialName("claimed")
        val chunkClaimed: StringDesc.Raw = prefix
            .plus("Вы заняли чанк!")
            .toRaw(),
        @SerialName("unclaimed")
        val chunkUnClaimed: StringDesc.Raw = prefix
            .plus("Чанк свободен!")
            .toRaw(),
        @SerialName("error")
        val error: StringDesc.Raw = prefix
            .plus("Ошибка! Смотрите консоль")
            .toRaw(),
        @SerialName("map")
        val blockMap: StringDesc.Raw = prefix
            .plus("Карта блоков:")
            .toRaw(),
        @SerialName("member_added")
        val memberAdded: StringDesc.Raw = prefix
            .plus("Участник добавлен")
            .toRaw(),
        @SerialName("member_removed")
        val memberRemoved: StringDesc.Raw = prefix
            .plus("Участник удален")
            .toRaw(),
        @SerialName("action_blocked")
        private val actionIsBlockByAdminClaim: StringDesc.Raw = prefix
            .plus("Ошибка! Действией %action% заблокировано на этом чанке!")
            .toRaw(),
    ) {
        fun actionIsBlockByAdminClaim(action: String): StringDesc {
            return actionIsBlockByAdminClaim.replace("%action%", action)
        }
    }

    @Serializable
    class Sit(
        @SerialName("prefix")
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BSIT&7] "),
        @SerialName("already")
        val sitAlready: StringDesc.Raw = prefix
            .plus("&#dbbb18Вы уже сидите")
            .toRaw(),
        @SerialName("air")
        val sitInAir: StringDesc.Raw = prefix
            .plus("&#dbbb18Нельзя сидеть в воздухе")
            .toRaw(),
        @SerialName("too_far")
        val tooFar: StringDesc.Raw = prefix
            .plus("&#dbbb18Слишком далеко")
            .toRaw(),
        @SerialName("cant_sit_in_block")
        val cantSitInBlock: StringDesc.Raw = prefix
            .plus("&#dbbb18Нельзя сидеть в блоке")
            .toRaw()
    )

    @Serializable
    class NewBee(
        @SerialName("prefix")
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BЗАЩИТА&7] "),
        val youAreNewBee: StringDesc.Raw = prefix
            .plus("&#1D72F2Вы новичок! &6Поэтому в первые 50 минут вам будет играть легче! Наслаждайтесь игрой!")
            .toRaw(),
        val newBeeTitle: StringDesc.Raw = StringDesc.Raw("&#DBB72BЗащита новичка"),
        val newBeeSubtitle: StringDesc.Raw = StringDesc.Raw("&#db2c18Включена"),
        val newBeeShieldForceDisabled: StringDesc.Raw = prefix
            .plus("Вы вступили в бой с игроком. Защита новичка была удалена")
            .toRaw()
    )

    @Serializable
    class Swear(
        val prefix: StringDesc.Raw = StringDesc.Raw("&7[&#DBB72BSF&7] "),
        val swearFilterEnabled: StringDesc.Raw = prefix
            .plus("Фильтр плохих слов включен")
            .toRaw(),
        val swearFilterDisabled: StringDesc.Raw = prefix
            .plus("Фильтр плохих слов выключен")
            .toRaw(),
        private val swearFilterEnabledFor: StringDesc.Raw = prefix
            .plus("Фильтр плохих слов включен для {player}")
            .toRaw(),
        private val swearFilterDisabledFor: StringDesc.Raw = prefix
            .plus("Фильтр плохих слов выключен для {player}")
            .toRaw(),
    ) {
        fun swearFilterEnabledFor(name: String) = swearFilterEnabledFor.replace("{player}", name)
        fun swearFilterDisabledFor(name: String) = swearFilterDisabledFor.replace("{player}", name)
    }
}

private fun StringDesc.toRaw(): StringDesc.Raw {
    return StringDesc.Raw(this.raw)
}
