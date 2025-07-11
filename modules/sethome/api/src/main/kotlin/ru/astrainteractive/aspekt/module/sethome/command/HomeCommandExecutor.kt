package ru.astrainteractive.aspekt.module.sethome.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.astralibs.server.MinecraftNativeBridge
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.util.getValue
import ru.astrainteractive.klibs.kstorage.util.update

class HomeCommandExecutor(
    private val homeKrateProvider: HomeKrateProvider,
    private val scope: CoroutineScope,
    private val translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
    minecraftNativeBridge: MinecraftNativeBridge
) : CommandExecutor<HomeCommand>,
    MinecraftNativeBridge by minecraftNativeBridge,
    KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate
    override fun execute(input: HomeCommand) {
        when (input) {
            is HomeCommand.DelHome -> {
                val krate = homeKrateProvider.get(input.playerData.uuid)
                scope.launch {
                    val home = krate
                        .getValue()
                        .firstOrNull { home -> home.name == input.homeName }
                    if (home == null) {
                        input.playerData.asAudience().sendMessage(translation.homes.homeNotFound.component)
                        return@launch
                    }
                    krate.update { homes -> homes.filter { home.name != input.homeName } }

                    input.playerData.asAudience().sendMessage(translation.homes.homeDeleted.component)
                }
            }

            is HomeCommand.SetHome -> {
                val krate = homeKrateProvider.get(input.playerData.uuid)
                scope.launch {
                    krate.update { homes -> homes.plus(input.playerHome) }

                    input.playerData.asAudience().sendMessage(translation.homes.homeCreated.component)
                }
            }

            is HomeCommand.TpHome -> {
                val krate = homeKrateProvider.get(input.playerData.uuid)
                scope.launch {
                    val home = krate
                        .getValue()
                        .firstOrNull { home -> home.name == input.homeName }
                    if (home == null) {
                        input.playerData.asAudience().sendMessage(translation.homes.homeNotFound.component)
                        return@launch
                    }
                    input.playerData
                        .asTeleportable()
                        .teleport(home.location)
                    input.playerData.asAudience().sendMessage(translation.homes.teleporting.component)
                }
            }
        }
    }
}
