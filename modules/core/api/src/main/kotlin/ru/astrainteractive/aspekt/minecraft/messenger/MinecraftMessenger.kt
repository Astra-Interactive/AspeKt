package ru.astrainteractive.aspekt.minecraft.messenger

import ru.astrainteractive.aspekt.minecraft.player.MinecraftPlayer
import ru.astrainteractive.astralibs.string.StringDesc

interface MinecraftMessenger {
    fun send(player: MinecraftPlayer, stringDesc: StringDesc)
}
