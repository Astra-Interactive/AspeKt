package ru.astrainteractive.aspekt

import kotlinx.coroutines.cancel
import net.minecraftforge.fml.common.Mod
import ru.astrainteractive.aspekt.di.RootModule
import ru.astrainteractive.astralibs.lifecycle.ForgeLifecycleServer
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger
import javax.annotation.ParametersAreNonnullByDefault

@Mod("aspekt")
@ParametersAreNonnullByDefault
class ForgeEntryPoint :
    ForgeLifecycleServer(),
    Logger by JUtiltLogger("AspeKt-ForgeEntryPoint") {
    private val rootModule = RootModule()

    override fun onEnable() {
        rootModule.lifecycle.onEnable()
    }

    override fun onDisable() {
        rootModule.lifecycle.onDisable()
        rootModule.coreModule.unconfinedScope.cancel()
    }

    override fun onReload() {
        rootModule.lifecycle.onReload()
    }
}
