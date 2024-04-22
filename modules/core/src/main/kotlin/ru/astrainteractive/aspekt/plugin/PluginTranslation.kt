@file:Suppress("MaxLineLength", "MaximumLineLength", "LongParameterList")

package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.StringDescExt.replace

/**
 * All translation stored here
 */
@Serializable
class PluginTranslation(
    @SerialName("general")
    val general: General = General(),
    @SerialName("sit")
    val sit: Sit = Sit(),
    @SerialName("adminprivate")
    val adminPrivate: AdminPrivate = AdminPrivate(),
    @SerialName("newbee")
    val newBee: NewBee = NewBee()
) {
    @Serializable
    class General(
        @SerialName("prefix")
        val prefix: StringDesc.Raw = StringDesc.Raw("&#18dbd1[AspeKt]"),
        @SerialName("reload")
        val reload: StringDesc.Raw = StringDesc.Raw("&#dbbb18Перезагрузка плагина"),
        @SerialName("reload_complete")
        val reloadComplete: StringDesc.Raw = StringDesc.Raw("&#42f596Перезагрузка успешно завершена"),
        @SerialName("no_permission")
        val noPermission: StringDesc.Raw = StringDesc.Raw("&#db2c18У вас нет прав!"),
        @SerialName("not_enough_money")
        val notEnoughMoney: StringDesc.Raw = StringDesc.Raw("&#db2c18Недостаточно средств!"),
        @SerialName("wrong_usage")
        val wrongUsage: StringDesc.Raw = StringDesc.Raw("&#db2c18Неверное использование!"),
        @SerialName("only_player_command")
        val onlyPlayerCommand: StringDesc.Raw = StringDesc.Raw("&#db2c18Эта команда только для игроков!"),
        @SerialName("menu_not_found")
        val menuNotFound: StringDesc.Raw = StringDesc.Raw("&#db2c18Меню с заданным ID не найдено"),
        @SerialName("discord_link_reward")
        private val discordLinkReward: StringDesc.Raw = StringDesc.Raw(
            "&#42f596Вы получили {AMOUNT}$ за привязку дискорда!"
        ),
        @SerialName("picked_up_money")
        private val pickedUpMoney: StringDesc.Raw = StringDesc.Raw("&#42f596Вы подобрали {AMOUNT} монет"),
        @SerialName("dropped_money")
        val droppedMoney: StringDesc.Raw = StringDesc.Raw("&6Монетка"),
        @SerialName("maybe_tpr")
        val maybeTpr: StringDesc.Raw = StringDesc.Raw("&#db2c18Возможно, вы хотели ввести /tpr")
    ) {

        fun discordLinkReward(amount: Number): StringDesc.Raw {
            return discordLinkReward.replace("{AMOUNT}", "${amount.toInt()}")
        }

        fun pickedUpMoney(amount: Number): StringDesc.Raw {
            return pickedUpMoney.replace("{AMOUNT}", "${amount.toInt()}")
        }
    }

    @Serializable
    class AdminPrivate(
        // Admin claim
        @SerialName("flag_changed")
        val chunkFlagChanged: StringDesc.Raw = StringDesc.Raw("&#db2c18Флаг чанка изменен!"),
        @SerialName("claimed")
        val chunkClaimed: StringDesc.Raw = StringDesc.Raw("&#db2c18Вы заняли чанк!"),
        @SerialName("unclaimed")
        val chunkUnClaimed: StringDesc.Raw = StringDesc.Raw("&#db2c18Чанк свободен!"),
        @SerialName("error")
        val error: StringDesc.Raw = StringDesc.Raw("&#db2c18Ошибка! Смотрите консоль"),
        @SerialName("map")
        val blockMap: StringDesc.Raw = StringDesc.Raw("&#18dbd1Карта блоков:"),
        @SerialName("action_blocked")
        private val actionIsBlockByAdminClaim: StringDesc.Raw = StringDesc.Raw(
            "&#db2c18Ошибка! Действией %action% заблокировано на этом чанке!"
        ),
    ) {
        fun actionIsBlockByAdminClaim(action: String): StringDesc.Raw {
            return actionIsBlockByAdminClaim.replace("%action%", action)
        }
    }

    @Serializable
    class Sit(
        @SerialName("already")
        val sitAlready: StringDesc.Raw = StringDesc.Raw("&#dbbb18Вы уже сидите"),
        @SerialName("air")
        val sitInAir: StringDesc.Raw = StringDesc.Raw("&#dbbb18Нельзя сидеть в воздухе")
    )

    @Serializable
    class NewBee(
        val youAreNewBee: StringDesc.Raw = StringDesc.Raw(
            "&7[&#DBB72BЗАЩИТА&7] &#1D72F2Вы новичок! &6Поэтому в ближайшие 50 минут вам будет играть легче! Наслаждайтесь игрой!"
        ),
        val newBeeTitle: StringDesc.Raw = StringDesc.Raw("&#DBB72BЗащита новичка"),
        val newBeeSubtitle: StringDesc.Raw = StringDesc.Raw("&#db2c18Включена")
    )
}
