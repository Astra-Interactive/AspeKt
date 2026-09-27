package ru.astrainteractive.aspekt.module.sethome.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.module.sethome.model.SetHomeConfiguration
import ru.astrainteractive.aspekt.plugin.PluginPermission
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.server.player.OnlineKPlayer
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers

@Suppress("LongParameterList")
internal class HomeCommandExecutor(
    private val homeKrateProvider: HomeKrateProvider,
    private val scope: CoroutineScope,
    private val dispatchers: KotlinDispatchers,
    translationKrate: CachedKrate<PluginTranslation>,
    setHomeConfigKrate: CachedKrate<SetHomeConfiguration>
) {
    private val translation by translationKrate
    private val setHomeConfig by setHomeConfigKrate

    /**
     * Per-role limit: the highest `aspekt.sethome.<count>` node the player holds wins over the
     * server-wide [SetHomeConfiguration.maxHomes] default.
     */
    private fun maxHomesOf(player: OnlineKPlayer): Int {
        return player.maxPermissionSize(PluginPermission.SET_HOME) ?: setHomeConfig.maxHomes
    }

    private suspend fun setHome(input: HomeCommand.SetHome) {
        val krate = homeKrateProvider.get(input.playerData.uuid)
        val homes = krate.getValue()
        val isOverride = homes.any { home -> home.name == input.playerHome.name }
        if (isOverride && !input.force) {
            input.playerData.sendMessage(translation.homes.alreadyExists)
            return
        }
        val maxHomes = maxHomesOf(input.playerData)
        if (!isOverride && homes.size >= maxHomes) {
            input.playerData.sendMessage(translation.homes.limitReached(maxHomes))
            return
        }
        krate.save { savedHomes ->
            savedHomes
                .filterNot { home -> home.name == input.playerHome.name }
                .plus(input.playerHome)
        }
        val message = when {
            isOverride -> translation.homes.overwritten
            else -> translation.homes.created
        }
        input.playerData.sendMessage(message)
    }

    private suspend fun delHome(input: HomeCommand.DelHome) {
        val krate = homeKrateProvider.get(input.playerData.uuid)
        val homeExists = krate.getValue().any { home -> home.name == input.homeName }
        if (!homeExists) {
            input.playerData.sendMessage(translation.homes.notFound)
            return
        }
        krate.save { homes -> homes.filterNot { home -> home.name == input.homeName } }
        input.playerData.sendMessage(translation.homes.deleted)
    }

    private suspend fun tpHome(input: HomeCommand.TpHome) {
        val home = homeKrateProvider
            .get(input.playerData.uuid)
            .getValue()
            .firstOrNull { home -> home.name == input.homeName }
        if (home == null) {
            input.playerData.sendMessage(translation.homes.notFound)
            return
        }
        // Entity teleportation is only legal on the server's main thread
        withContext(dispatchers.Main) {
            input.playerData.teleport(home.location)
        }
        input.playerData.sendMessage(translation.homes.teleported)
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
