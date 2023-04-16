package ru.astrainteractive.aspekt.filemanager

import java.io.File
import java.net.URL

class DefaultFileManager(
    override val name: String,
    override val dataFolder: File
) : FileManager {
    private val resource: URL?
        get() = object {}.javaClass.getResource(name)
    override val file: File
        get() = File(dataFolder, name)

    override val isResourceExists: Boolean = resource != null


    override fun saveFromResource(fileName: String) {
        val text = resource!!.readText()
        if (!file.exists()) {
            file.mkdirs()
            file.createNewFile()
        }
        println(text)
        file.writeText(text)
    }

    override fun loadConfigFile() {
        var file = File(dataFolder, name)
        if (file.exists()) return
        if (isResourceExists) {
            saveFromResource(name)
            return
        }
        file = File(dataFolder, name)
        file.parentFile?.mkdirs()
        file.createNewFile()
        return
    }

    init {
        loadConfigFile()
    }
}