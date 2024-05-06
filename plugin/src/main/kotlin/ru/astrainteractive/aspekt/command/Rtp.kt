package ru.astrainteractive.aspekt.command

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.RandomTeleport
import org.bukkit.Bukkit
import ru.astrainteractive.aspekt.util.FixedLegacySerializer

fun CommandManager.rtp() = plugin.getCommand("rtp")?.setExecutor { sender, command, label, args ->
    translation.general.maybeTpr
        .let(FixedLegacySerializer::toComponent)
        .run(sender::sendMessage)
    true
}

fun CommandManager.rtpBypassed() = plugin.getCommand("rtpbypass")?.setExecutor { sender, command, label, args ->
    val player = args.getOrNull(0)?.let(Bukkit::getPlayer) ?: return@setExecutor true
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
    true
}
