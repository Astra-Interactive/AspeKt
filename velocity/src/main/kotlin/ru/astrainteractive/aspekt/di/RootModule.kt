package ru.astrainteractive.aspekt.di

import com.google.inject.Injector
import com.velocitypowered.api.proxy.ProxyServer
import ru.astrainteractive.aspekt.plugin.Configuration
import ru.astrainteractive.astralibs.configloader.ConfigLoader
import ru.astrainteractive.astralibs.filemanager.impl.JVMFileManager
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import ru.astrainteractive.klibs.kdi.getValue
import java.nio.file.Path
import java.util.logging.Logger

object RootModule {
    val injector = Lateinit<Injector>()
    val server = Lateinit<ProxyServer>()
    val logger = Lateinit<Logger>()
    val dataDirectory = Lateinit<Path>()
    val configurationFile = Single {
        val dataDirectory by dataDirectory
        JVMFileManager("config.yml", dataDirectory.toFile())
    }
    val configuration = Reloadable {
        val configurationFile by configurationFile
        ConfigLoader().toClassOrDefault<Configuration>(file = configurationFile.configFile, default = ::Configuration)
    }
}
