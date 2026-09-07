package ru.astrainteractive.aspekt.module.sethome.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.kyori.unwrap
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

internal class HomeCommandExecutor(
    private val homeKrateProvider: HomeKrateProvider,
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    translationKrate: CachedKrate<PluginTranslation>,
    kyoriKrate: CachedKrate<KyoriComponentSerializer>,
) : KyoriComponentSerializer by kyoriKrate.unwrap() {
    private val translation by translationKrate

    private suspend fun setHome(input: HomeCommand.SetHome) {
        homeKrateProvider
            .get(input.playerData.uuid)
            .save { homes ->
                homes
                    .filterNot { home -> home.name == input.playerHome.name }
                    .plus(input.playerHome)
            }
        input.playerData.sendMessage(translation.homes.homeCreated.component)
    }

    private suspend fun delHome(input: HomeCommand.DelHome) {
        val krate = homeKrateProvider.get(input.playerData.uuid)
        val homeExists = krate.getValue().any { home -> home.name == input.homeName }
        if (!homeExists) {
            input.playerData.sendMessage(translation.homes.homeNotFound.component)
            return
        }
        krate.save { homes -> homes.filterNot { home -> home.name == input.homeName } }
        input.playerData.sendMessage(translation.homes.homeDeleted.component)
    }

    private suspend fun tpHome(input: HomeCommand.TpHome) {
        val home = homeKrateProvider
            .get(input.playerData.uuid)
            .getValue()
            .firstOrNull { home -> home.name == input.homeName }
        if (home == null) {
            input.playerData.sendMessage(translation.homes.homeNotFound.component)
            return
        }
        // Entity teleportation is only legal on the server's main thread
        withContext(dispatchers.Main) {
            input.playerData.teleport(home.location)
        }
        input.playerData.sendMessage(translation.homes.teleporting.component)
    }

    fun execute(input: HomeCommand) {
        scope.launch {
            when (input) {
                is HomeCommand.SetHome -> setHome(input)
                is HomeCommand.DelHome -> delHome(input)
                is HomeCommand.TpHome -> tpHome(input)
            }
        }
    }
}
