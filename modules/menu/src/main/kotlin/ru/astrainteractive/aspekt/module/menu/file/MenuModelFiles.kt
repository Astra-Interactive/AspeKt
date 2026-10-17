package ru.astrainteractive.aspekt.module.menu.file

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.astralibs.util.parse
import java.io.File

internal fun readMenuModels(dataFolder: File, yamlFormat: StringFormat): List<MenuModel> {
    val menuFolder = File(dataFolder, "menu")
    if (!menuFolder.exists()) menuFolder.mkdirs()
    return menuFolder.listFiles()
        .orEmpty()
        .filter(File::exists)
        .filter(File::isFile)
        .mapNotNull { file -> yamlFormat.parse<MenuModel>(file).getOrNull() }
}
