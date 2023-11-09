@file:Suppress("MaxLineLength", "MaximumLineLength", "LongParameterList")

package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.replace

/**
 * All translation stored here
 */
@Serializable
class PluginTranslation(
    @SerialName("getByByCheck")
    val getByByCheck: StringDesc.Raw = StringDesc.Raw("#db2c18getByByCheck"),
    // Database
    @SerialName("database.success")
    val dbSuccess: StringDesc.Raw = StringDesc.Raw("#18dbd1Успешно подключено к базе данных"),
    @SerialName("database.fail")
    val dbFail: StringDesc.Raw = StringDesc.Raw("#db2c18Нет подключения к базе данных"),
    // General
    @SerialName("general.prefix")
    val prefix: StringDesc.Raw = StringDesc.Raw("#18dbd1[AspeKt]"),
    @SerialName("general.reload")
    val reload: StringDesc.Raw = StringDesc.Raw("#dbbb18Перезагрузка плагина"),
    @SerialName("general.reload_complete")
    val reloadComplete: StringDesc.Raw = StringDesc.Raw("#42f596Перезагрузка успешно завершена"),
    @SerialName("general.no_permission")
    val noPermission: StringDesc.Raw = StringDesc.Raw("#db2c18У вас нет прав!"),
    @SerialName("general.not_enough_money")
    val notEnoughMoney: StringDesc.Raw = StringDesc.Raw("#db2c18Недостаточно средств!"),
    @SerialName("general.wrong_usage")
    val wrongUsage: StringDesc.Raw = StringDesc.Raw("#db2c18Неверное использование!"),
    @SerialName("general.only_player_command")
    val onlyPlayerCommand: StringDesc.Raw = StringDesc.Raw("#db2c18Эта команда только для игроков!"),
    @SerialName("general.menu_not_found")
    val menuNotFound: StringDesc.Raw = StringDesc.Raw("#db2c18Меню с заданным ID не найдено"),
    @SerialName("general.discord_link_reward")
    private val discordLinkReward: StringDesc.Raw = StringDesc.Raw("#42f596Вы получили {AMOUNT}$ за привязку дискорда!"),
    // Admin claim
    @SerialName("general.adminprivate.flag_changed")
    val chunkFlagChanged: StringDesc.Raw = StringDesc.Raw("#db2c18Флаг чанка изменен!"),
    @SerialName("general.adminprivate.claimed")
    val chunkClaimed: StringDesc.Raw = StringDesc.Raw("#db2c18Вы заняли чанк!"),
    @SerialName("general.adminprivate.unclaimed")
    val chunkUnClaimed: StringDesc.Raw = StringDesc.Raw("#db2c18Чанк свободен!"),
    @SerialName("general.adminprivate.error")
    val error: StringDesc.Raw = StringDesc.Raw("#db2c18Ошибка! Смотрите консоль"),
    @SerialName("general.adminprivate.map")
    val blockMap: StringDesc.Raw = StringDesc.Raw("#18dbd1Карта блоков:"),
    @SerialName("general.adminprivate.action_blocked")
    private val actionIsBlockByAdminClaim: StringDesc.Raw = StringDesc.Raw(
        "#db2c18Ошибка! Действией %action% заблокировано на этом чанке!"
    ),
    // Sit
    @SerialName("sit.already") val sitAlready: StringDesc.Raw = StringDesc.Raw("#dbbb18Вы уже сидите"),
    @SerialName("sit.air") val sitInAir: StringDesc.Raw = StringDesc.Raw("#dbbb18Нельзя сидеть в воздухе")
) {

    fun actionIsBlockByAdminClaim(action: String): StringDesc.Raw {
        return actionIsBlockByAdminClaim.replace("%action%", action)
    }

    fun discordLinkReward(amount: Number): StringDesc.Raw {
        return discordLinkReward.replace("{AMOUNT}", "${amount.toInt()}")
    }
}
