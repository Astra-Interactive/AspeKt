package ru.astrainteractive.astraessentials.plugin

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.astraessentials.utils.cBoolean
import ru.astrainteractive.astraessentials.utils.cInt
import ru.astrainteractive.astraessentials.utils.cStringList
import ru.astrainteractive.astraessentials.utils.getValue
import ru.astrainteractive.astralibs.configuration.Configuration
import ru.astrainteractive.astralibs.di.Module
import kotlin.reflect.KProperty


class PluginConfiguration(private val fc: FileConfiguration) {
    val sit = fc.cBoolean("core.sit", true)
    val discordSRVLink = DiscordSRVLink()
    val announcements = Announcements()
    val autoCrop = AutoCrop()

    inner class AutoCrop {
        val enabled by fc.cBoolean("core.auto_crop.enabled", true)
        val minDrop by fc.cInt("core.auto_crop.min", 0)
        val maxDrop by fc.cInt("core.auto_crop.max", 1)
        val dupeProtection = DupeProtection()

        inner class DupeProtection {
            val enabled by fc.cBoolean("core.auto_crop.duping.enabled", true)
            val clearEveryMs by fc.cInt("core.auto_crop.duping.clear_every", 60_000)
            val locationTimeoutMs by fc.cInt("core.auto_crop.duping.location_timeout", 15_000)
        }
    }

    inner class Announcements {
        val interval = fc.cInt("announcements.interval", 10)
        val announcements = fc.cStringList("announcements.announcements")
    }

    inner class DiscordSRVLink {
        val discordOnLinked = Roles("on_linked.discord")
        val luckPermsOnLinked = Roles("on_linked.luckperms")
        val discordOnUnLinked = Roles("on_unlinked.discord")
        val luckPermsOnUnLinked = Roles("on_unlinked.luckperms")

        inner class Roles(section: String) {
            val addRoles = fc.cStringList("discordsrv.$section.add_roles")
            val removeRoles = fc.cStringList("discordsrv.$section.remove_roles")
        }
    }
}
