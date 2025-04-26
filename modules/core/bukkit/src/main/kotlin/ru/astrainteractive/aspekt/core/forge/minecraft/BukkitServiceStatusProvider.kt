package ru.astrainteractive.aspekt.core.forge.minecraft

import ru.astrainteractive.aspekt.minecraft.ServiceStatusProvider

object BukkitServiceStatusProvider : ServiceStatusProvider {
    override fun isReady(): Boolean {
        return runCatching { Class.forName("org.bukkit.Bukkit") }
            .map { org.bukkit.Bukkit.getServer() != null }
            .getOrElse { false }
    }
}
