package ru.astrainteractive.aspekt.filemanager

import java.io.File

interface FileManager {
    val name: String
    val dataFolder: File

    val file: File
    val isResourceExists: Boolean
    fun saveFromResource(fileName: String)
    fun loadConfigFile()
}

