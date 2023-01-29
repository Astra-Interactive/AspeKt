package ru.astrainteractive.astraessentials.plugin

import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astralibs.utils.BaseTranslation

/**
 * All translation stored here
 */
class PluginTranslation : BaseTranslation() {
    /**
     * This is a default translation file. Don't forget to create translation.yml in resources of the plugin
     */
    protected override val translationFile: FileManager = FileManager("translations.yml")


    val getByByCheck = translationValue("getByByCheck", "#db2c18getByByCheck")

    //Database
    val dbSuccess = translationValue("database.success", "#18dbd1Успешно подключено к базе данных")
    val dbFail = translationValue("database.fail", "#db2c18Нет подключения к базе данных")

    //General
    val prefix = translationValue("general.prefix", "#18dbd1[AstraEssentials]")
    val reload = translationValue("general.reload", "#dbbb18Перезагрузка плагина")
    val reloadComplete = translationValue("general.reload_complete", "#42f596Перезагрузка успешно завершена")
    val noPermission = translationValue("general.no_permission", "#db2c18У вас нет прав!")

    // Sit
    public val sitAlready: String = translationValue("sit.already", "#dbbb18Вы уже сидите")
    public val sitInAir: String = translationValue("sit.air", "#dbbb18Нельзя сидеть в воздухе")

}


