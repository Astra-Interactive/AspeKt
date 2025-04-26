package ru.astrainteractive.aspekt.core.forge.minecraft

import ru.astrainteractive.aspekt.core.forge.util.ForgeUtil
import ru.astrainteractive.aspekt.minecraft.ServiceStatusProvider

object ForgeServiceStatusProvider : ServiceStatusProvider {
    override fun isReady(): Boolean {
        return ForgeUtil.serverOrNull != null
    }
}
