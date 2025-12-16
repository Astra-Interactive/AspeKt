package ru.astrainteractive.aspekt

import net.neoforged.fml.common.Mod
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.astralibs.lifecycle.ForgeLifecycleServer
import ru.astrainteractive.astralibs.server.util.NeoForgeUtil
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import javax.annotation.ParametersAreNonnullByDefault

@Mod("aspekt")
@ParametersAreNonnullByDefault
class ForgeEntryPoint :
    Logger by JUtiltLogger("AspeKt-ForgeEntryPoint"),
    ForgeLifecycleServer() {
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

    init {
        NeoForgeUtil.bootstrap()
    }
}
