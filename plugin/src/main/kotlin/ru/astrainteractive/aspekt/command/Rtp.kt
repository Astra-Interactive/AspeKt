package ru.astrainteractive.aspekt.command

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.RandomTeleport
import org.bukkit.Bukkit
import ru.astrainteractive.astralibs.commands.registerCommand
import ru.astrainteractive.astralibs.commands.types.PrimitiveArgumentType
import ru.astrainteractive.astralibs.utils.hex

fun CommandManager.rtp() = plugin.registerCommand("rtp") {
    sender.sendMessage("#db2c18Возможно, вы хотели ввести /tpr".hex())
}

fun CommandManager.rtpBypassed() = plugin.registerCommand("rtpbypass") {
    val playerName = argument(0, PrimitiveArgumentType.String).resultOrNull() ?: return@registerCommand
    val player = Bukkit.getPlayer(playerName) ?: return@registerCommand
    val essentials = Bukkit.getPluginManager().getPlugin("Essentials") as Essentials
    val randomTeleport = RandomTeleport(essentials)
    val completable = randomTeleport.getRandomLocation(
        randomTeleport.center,
        randomTeleport.minRange,
        randomTeleport.maxRange
    )
    completable.whenComplete { location, _ ->
        player.teleport(location)
    }
}
