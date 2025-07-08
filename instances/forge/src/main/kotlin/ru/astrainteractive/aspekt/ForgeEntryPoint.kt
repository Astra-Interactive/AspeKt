package ru.astrainteractive.aspekt

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.common.Mod
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.astralibs.event.flowEvent
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.server.util.ForgeUtil
import javax.annotation.ParametersAreNonnullByDefault

@Mod("aspekt")
@ParametersAreNonnullByDefault
class ForgeEntryPoint :
    Logger by JUtiltLogger("AspeKt-ForgeEntryPoint"),
    Lifecycle {
    private val rootModule by lazy { RootModule() }

    override fun onEnable() {
        rootModule.lifecycle.onEnable()
    }

    override fun onDisable() {
        info { "#onDisable" }
        rootModule.lifecycle.onDisable()
    }

    override fun onReload() {
        rootModule.lifecycle.onReload()
    }

    val serverStartedEvent = flowEvent<ServerStartedEvent>(EventPriority.HIGHEST)
        .onEach {
            info { "#serverStartedEvent" }
            onEnable()
        }.launchIn(rootModule.scope)

    val serverStoppingEvent = flowEvent<ServerStoppingEvent>(EventPriority.HIGHEST)
        .onEach {
            info { "#serverStoppingEvent" }
            onDisable()
        }.launchIn(rootModule.scope)

    val registerCommandsEvent = flowEvent<RegisterCommandsEvent>(EventPriority.HIGHEST)
        .onEach { e ->
            info { "#registerCommandsEvent" }
        }.launchIn(rootModule.scope)

    init {
        ForgeUtil.bootstrap()
    }
}
