package ru.astrainteractive.aspekt.core.forge.util

import com.mojang.authlib.GameProfile
import net.minecraft.server.level.ServerPlayer
import ru.astrainteractive.aspekt.minecraft.player.OfflineMinecraftPlayer
import ru.astrainteractive.aspekt.minecraft.player.OnlineMinecraftPlayer

fun ServerPlayer.asOnlineMinecraftPlayer(): OnlineMinecraftPlayer {
    return OnlineMinecraftPlayer(
        uuid = uuid,
        name = name.toPlain(),
        ipAddress = ipAddress
    )
}

fun GameProfile.asOfflineMinecraftPlayer(): OfflineMinecraftPlayer {
    return OfflineMinecraftPlayer(
        uuid = id
    )
}
