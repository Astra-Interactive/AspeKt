package ru.astrainteractive.aspekt.core.forge.util

import net.minecraft.world.entity.player.Player
import ru.astrainteractive.aspekt.core.forge.permission.ForgeLuckPermsPlayerPermissible
import ru.astrainteractive.astralibs.permission.Permissible

fun Player.asPermissible(): Permissible {
    return if (ForgeUtil.isModLoaded("luckperms")) {
        ForgeLuckPermsPlayerPermissible(this.uuid)
    } else {
        error("No permission provider loaded!")
    }
}
