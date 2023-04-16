package ru.astrainteractive.aspekt.di

import com.google.inject.Injector
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import ru.astrainteractive.aspekt.plugin.Configuration
import ru.astrainteractive.astralibs.EmpireSerializer
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.reloadable
import java.nio.file.Path
import ru.astrainteractive.aspekt.AspeKt
import ru.astrainteractive.astralibs.di.Lateinit
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.filemanager.DefaultFileManager

object ServiceLocator {
    val injector = Lateinit<Injector>()
    val server = Lateinit<ProxyServer>()
    val logger = Lateinit<Logger>()
    val dataDirectory = Lateinit<Path>()
    val configurationFile = module {
        val dataDirectory by dataDirectory
        DefaultFileManager(AspeKt::class.java, "config.yml", dataDirectory.toFile())
    }
    val configuration = reloadable {
        val configurationFile by configurationFile
        EmpireSerializer.toClass<Configuration>(configurationFile.configFile) ?: Configuration()
    }
}