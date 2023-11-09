package ru.astrainteractive.aspekt.di.factory

import kotlinx.serialization.SerializationException
import ru.astrainteractive.aspekt.plugin.MenuModel
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.klibs.kdi.Factory
import java.io.File

class MenuModelFactory(private val file: File) : Factory<MenuModel> {
    /**
     * @throws SerializationException in case of any decoding-specific error
     * @throws IllegalArgumentException if the decoded input is not a valid instance of [MenuModel]
     */
    override fun create(): MenuModel {
        return YamlSerializer().unsafeParse(file)
    }
}