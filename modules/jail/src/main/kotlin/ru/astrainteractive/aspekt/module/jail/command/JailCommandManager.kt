package ru.astrainteractive.aspekt.module.jail.command

import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.aspekt.module.jail.command.jail.jailCommand
import ru.astrainteractive.aspekt.module.jail.controller.JailController
import ru.astrainteractive.aspekt.module.jail.data.CachedJailApi
import ru.astrainteractive.aspekt.module.jail.data.JailApi
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.klibs.kstorage.api.CachedKrate

@Suppress("LongParameterList")
internal class JailCommandManager(
    val scope: CoroutineScope,
    val plugin: JavaPlugin,
    val translationKrate: CachedKrate<PluginTranslation>,
    val kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    val jailApi: JailApi,
    val cachedJailApi: CachedJailApi,
    val jailController: JailController
) : Logger by JUtiltLogger("AspeKt-JailCommandManager") {
    fun register() {
        jailCommand()
    }
}
