package ru.astrainteractive.aspekt.core.forge.util

import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.entity.player.Player
import ru.astrainteractive.aspekt.minecraft.Audience

fun Player.asAudience() = Audience { component ->
    this.sendSystemMessage(component.toNative())
}

fun CommandSourceStack.asAudience() = Audience { component ->
    this.sendSystemMessage(component.toNative())
}
