package ru.astrainteractive.aspekt.module.sethome.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.astrainteractive.aspekt.minecraft.messenger.MinecraftMessenger
import ru.astrainteractive.aspekt.minecraft.teleport.TeleportApi
import ru.astrainteractive.aspekt.module.sethome.data.HomeKrateProvider
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.command.api.executor.CommandExecutor
import ru.astrainteractive.klibs.kstorage.api.Krate
import ru.astrainteractive.klibs.kstorage.util.KrateExt.update
import ru.astrainteractive.klibs.kstorage.util.getValue

class HomeCommandExecutor(
    private val homeKrateProvider: HomeKrateProvider,
    private val scope: CoroutineScope,
    private val teleportApi: TeleportApi,
    private val translationKrate: Krate<PluginTranslation>,
    private val minecraftMessenger: MinecraftMessenger,
) : CommandExecutor<HomeCommand> {
    private val translation by translationKrate
    override fun execute(input: HomeCommand) {
        when (input) {
            is HomeCommand.DelHome -> {
                val krate = homeKrateProvider.get(input.playerData.uuid)
                scope.launch {
                    val home = krate
                        .loadAndGet()
                        .firstOrNull { home -> home.name == input.homeName }
                    if (home == null) {
                        minecraftMessenger.send(input.playerData, translation.homes.homeNotFound)
                        return@launch
                    }
                    krate.update { homes -> homes.filter { home.name != input.homeName } }
                    minecraftMessenger.send(input.playerData, translation.homes.homeDeleted)
                }
            }

            is HomeCommand.SetHome -> {
                val krate = homeKrateProvider.get(input.playerData.uuid)
                scope.launch {
                    krate.update { homes -> homes.plus(input.playerHome) }
                    minecraftMessenger.send(input.playerData, translation.homes.homeCreated)
                }
            }

            is HomeCommand.TpHome -> {
                val krate = homeKrateProvider.get(input.playerData.uuid)
                scope.launch {
                    val home = krate
                        .loadAndGet()
                        .firstOrNull { home -> home.name == input.homeName }
                    if (home == null) {
                        minecraftMessenger.send(input.playerData, translation.homes.homeNotFound)
                        return@launch
                    }
                    teleportApi.teleport(input.playerData, home.location)
                    minecraftMessenger.send(input.playerData, translation.homes.teleporting)
                }
            }
        }
    }
}
