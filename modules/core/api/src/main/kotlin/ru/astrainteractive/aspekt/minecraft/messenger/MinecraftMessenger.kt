package ru.astrainteractive.aspekt.minecraft.messenger

import ru.astrainteractive.aspekt.minecraft.player.MinecraftPlayer
import ru.astrainteractive.astralibs.string.StringDesc
import java.util.UUID

interface MinecraftMessenger {
    fun send(player: MinecraftPlayer, stringDesc: StringDesc)
    fun send(uuid: UUID, stringDesc: StringDesc)
}
