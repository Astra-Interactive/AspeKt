package ru.astrainteractive.aspekt.di

import com.google.inject.Injector
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import ru.astrainteractive.aspekt.filemanager.DefaultFileManager
import ru.astrainteractive.aspekt.plugin.Configuration
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.reloadable
import java.io.File
import java.nio.file.Path

object ServiceLocator {
    val injector = Lateinit<Injector>()
    val server = Lateinit<ProxyServer>()
    val logger = Lateinit<Logger>()
    val dataDirectory = Lateinit<Path>()
    val configurationFile = reloadable {
        val dataDirectionality by dataDirectory
        DefaultFileManager("config.yml", dataDirectionality.toFile())
    }
    val configuration = reloadable {
        val configurationFile by configurationFile
        EmpireSerializer.toClass<Configuration>(configurationFile.file) ?: Configuration()
    }
}