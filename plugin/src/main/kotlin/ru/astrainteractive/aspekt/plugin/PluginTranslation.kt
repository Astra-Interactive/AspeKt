package ru.astrainteractive.aspekt.plugin

import org.bukkit.plugin.Plugin
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.astralibs.di.getValue
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
    protected override val translationFile: SpigotFileManager = DefaultSpigotFileManager(plugin,"translations.yml")


    val getByByCheck = translationValue("getByByCheck", "#db2c18getByByCheck")

    //Database
    val dbSuccess = translationValue("database.success", "#18dbd1Успешно подключено к базе данных")
    val dbFail = translationValue("database.fail", "#db2c18Нет подключения к базе данных")

    //General
    val prefix = translationValue("general.prefix", "#18dbd1[AspeKt]")
    val reload = translationValue("general.reload", "#dbbb18Перезагрузка плагина")
    val reloadComplete = translationValue("general.reload_complete", "#42f596Перезагрузка успешно завершена")
    val noPermission = translationValue("general.no_permission", "#db2c18У вас нет прав!")

    // Sit
    public val sitAlready: String = translationValue("sit.already", "#dbbb18Вы уже сидите")
    public val sitInAir: String = translationValue("sit.air", "#dbbb18Нельзя сидеть в воздухе")

}


