package ru.astrainteractive.aspekt.di

import com.google.inject.Injector
import com.velocitypowered.api.proxy.ProxyServer
import ru.astrainteractive.aspekt.plugin.Configuration
import ru.astrainteractive.astralibs.filemanager.impl.JVMFileManager
import ru.astrainteractive.astralibs.serialization.SerializerExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.YamlSerializer
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
        YamlSerializer().parseOrDefault<Configuration>(file = configurationFile.configFile, factory = ::Configuration)
    }
}
