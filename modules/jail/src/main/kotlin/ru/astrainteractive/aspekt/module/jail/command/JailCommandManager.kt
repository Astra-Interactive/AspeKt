package ru.astrainteractive.aspekt.module.jail.command

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.jail.command.jail.jailCommand
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.mikro.core.logging.JUtiltLogger
import ru.astrainteractive.klibs.mikro.core.logging.Logger

@Suppress("LongParameterList")
internal class JailCommandManager(
    val scope: CoroutineScope,
    val plugin: JavaPlugin,
    val translationKrate: CachedKrate<PluginTranslation>,
    val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    val jailApi: JailApi,
    val cachedJailApi: CachedJailApi,
    val jailController: JailController,
    val commandsRegistrarFlow: SharedFlow<ReloadableRegistrarEvent<Commands>?>,
    val mainScope: CoroutineScope
) : Logger by JUtiltLogger("AspeKt-JailCommandManager") {
    fun register() {
        commandsRegistrarFlow
            .mapNotNull { it?.registrar() }
            .onEach { it.register(jailCommand().build()) }
            .launchIn(mainScope)
    }
}
