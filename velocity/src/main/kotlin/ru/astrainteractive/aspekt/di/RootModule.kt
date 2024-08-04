package ru.astrainteractive.aspekt.di

import com.google.inject.Injector
import com.velocitypowered.api.proxy.ProxyServer
import ru.astrainteractive.aspekt.plugin.Configuration
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parseOrDefault
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kdi.Lateinit
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.getValue
import java.nio.file.Path
import java.util.logging.Logger

object RootModule {
    val injector = Lateinit<Injector>()
    val server = Lateinit<ProxyServer>()
    val logger = Lateinit<Logger>()
    val dataDirectory = Lateinit<Path>()
    val configurationFile by lazy {
        val dataDirectory by dataDirectory
        dataDirectory.toFile().resolve("config.yml")
    }
    val configuration = Reloadable {
        YamlStringFormat().parseOrDefault<Configuration>(file = configurationFile, factory = ::Configuration)
    }
}
