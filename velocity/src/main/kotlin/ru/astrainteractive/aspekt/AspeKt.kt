package ru.astrainteractive.aspekt

import com.google.inject.Inject
import com.google.inject.Injector
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.lifecycle.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@Plugin(
    id = BuildKonfig.id,
    name = BuildKonfig.name,
    version = BuildKonfig.version,
    url = BuildKonfig.url,
    description = BuildKonfig.description,
    authors = [BuildKonfig.author]
)
@Suppress("UnusedPrivateProperty")
class AspeKt @Inject constructor(
    injector: Injector,
    server: ProxyServer,
    logger: Logger,
    @DataDirectory dataDirectory: Path
) {
    init {
        val logger = java.util.logging.Logger.getAnonymousLogger()

        @Suppress("UnusedPrivateMember")
        val logsFolderPath = dataDirectory.absolutePathString()
//        RootModule.injector.initialize(injector)
//        RootModule.server.initialize(server)
//        RootModule.logger.initialize(logger)
//        RootModule.dataDirectory.initialize(dataDirectory)
        logger.info("Hello there! I made my first plugin with Velocity.")
//        logger.info("Here's your configuration: ${RootModule.configuration.value}.")
    }

    @Suppress("UnusedPrivateMember")
    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent?) {
        // Do some operation demanding access to the Velocity API here.
        // For instance, we could register an event:
//        server.eventManager.register(this, PluginListener())
    }

    fun reload() {
//        with(RootModule) {
//            configuration.reload()
//        }
    }
}
