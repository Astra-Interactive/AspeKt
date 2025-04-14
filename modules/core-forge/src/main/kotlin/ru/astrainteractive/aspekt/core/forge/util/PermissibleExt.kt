package ru.astrainteractive.aspekt.core.forge.util

import net.minecraft.world.entity.player.Player
import ru.astrainteractive.aspekt.core.forge.permission.ForgeLuckPermsPlayerPermissible
import ru.astrainteractive.aspekt.core.forge.permission.ForgePermissible
import ru.astrainteractive.astralibs.permission.Permissible

fun Player.toPermissible(): Permissible {
    return if (ForgeUtil.isModLoaded("luckperms")) {
        ForgeLuckPermsPlayerPermissible(this.uuid)
    } else {
        ForgePermissible(this.uuid)
    }
}
