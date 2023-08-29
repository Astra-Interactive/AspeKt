package ru.astrainteractive.aspekt.plugin

import org.bukkit.plugin.Plugin
import ru.astrainteractive.astralibs.filemanager.DefaultSpigotFileManager
import ru.astrainteractive.astralibs.filemanager.SpigotFileManager
import ru.astrainteractive.astralibs.utils.BaseTranslation

/**
 * All translation stored here
 */
class PluginTranslation(plugin: Plugin) : BaseTranslation() {
    /**
     * This is a default translation file. Don't forget to create translation.yml in resources of the plugin
     */
    protected override val translationFile: SpigotFileManager = DefaultSpigotFileManager(plugin, "translations.yml")

    val getByByCheck = translationValue("getByByCheck", "#db2c18getByByCheck")

    // Database
    val dbSuccess = translationValue("database.success", "#18dbd1Успешно подключено к базе данных")
    val dbFail = translationValue("database.fail", "#db2c18Нет подключения к базе данных")

    // General
    val prefix = translationValue("general.prefix", "#18dbd1[AspeKt]")
    val reload = translationValue("general.reload", "#dbbb18Перезагрузка плагина")
    val reloadComplete = translationValue("general.reload_complete", "#42f596Перезагрузка успешно завершена")
    val noPermission = translationValue("general.no_permission", "#db2c18У вас нет прав!")
    val notEnoughMoney = translationValue("general.not_enough_money", "#db2c18Недостаточно средств!")
    val wrongUsage = translationValue("general.wrong_usage", "#db2c18Неверное использование!")
    val onlyPlayerCommand = translationValue("general.only_player_command", "#db2c18Эта команда только для игроков!")
    val menuNotFound = translationValue("general.menu_not_found", "#db2c18Меню с заданным ID не найдено")
    private val discordLinkReward = translationValue(
        "general.discord_link_reward",
        "#42f596Вы получили {AMOUNT}$ за привязку дискорда!"
    )
    fun discordLinkReward(amount: Number) = discordLinkReward.replace("{AMOUNT}", "${amount.toInt()}")

    // Admin claim
    val chunkFlagChanged = translationValue("general.adminprivate.flag_changed", "#db2c18Флаг чанка изменен!")
    val chunkClaimed = translationValue("general.adminprivate.claimed", "#db2c18Вы заняли чанк!")
    val chunkUnClaimed = translationValue("general.adminprivate.unclaimed", "#db2c18Чанк свободен!")
    val error = translationValue("general.adminprivate.error", "#db2c18Ошибка! Смотрите консоль")
    val blockMap = translationValue("general.adminprivate.map", "#18dbd1Карта блоков:")
    private val actionIsBlockByAdminClaim = translationValue(
        "general.adminprivate.action_blocked",
        "#db2c18Ошибка! Действией %action% заблокировано на этом чанке!"
    )

    fun actionIsBlockByAdminClaim(action: String) = actionIsBlockByAdminClaim.replace("%action%", action)

    // Sit
    public val sitAlready: String = translationValue("sit.already", "#dbbb18Вы уже сидите")
    public val sitInAir: String = translationValue("sit.air", "#dbbb18Нельзя сидеть в воздухе")
}
