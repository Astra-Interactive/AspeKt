package ru.astrainteractive.aspekt.module.menu.di.factory

import kotlinx.serialization.SerializationException
import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.menu.model.MenuModel
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import java.io.File

internal class MenuModelsFactory(
    private val dataFolder: File,
    private val yamlSerializer: StringFormat
) {
    /**
     * @throws SerializationException in case of any decoding-specific error
     * @throws IllegalArgumentException if the decoded input is not a valid instance of [MenuModel]
     */
    fun create(): List<MenuModel> {
        val dataFolder = dataFolder
        val menuFolder = File(dataFolder, "menu")
        if (!menuFolder.exists()) menuFolder.mkdirs()

        return menuFolder.listFiles()
            .orEmpty()
            .filterNotNull()
            .filter(File::exists)
            .filter(File::isFile)
            .mapNotNull { yamlSerializer.parse<MenuModel>(it).getOrNull() }
    }
}
