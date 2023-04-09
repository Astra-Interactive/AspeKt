package ru.astrainteractive.astraessentials.plugin

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.astraessentials.utils.cBoolean
import ru.astrainteractive.astraessentials.utils.cInt
import ru.astrainteractive.astraessentials.utils.cStringList
import ru.astrainteractive.astraessentials.utils.getValue


class PluginConfiguration(private val fc: FileConfiguration) {
    val sit by fc.cBoolean("core.sit", true)
    val discordSRVLink = DiscordSRVLink()
    val announcements = Announcements()
    val autoCrop = AutoCrop()
    val tc = TC()

    inner class TC {
        private val PATH: String = "core.tree_capitator"

        val enabled by fc.cBoolean("$PATH.enabled", true)
        val destroyLimit by fc.cInt("$PATH.destroy_limit", 16)
        val damageAxe by fc.cBoolean("$PATH.damage_axe", true)
        val breakAxe by fc.cBoolean("$PATH.break_axe", true)
        val replant by fc.cBoolean("$PATH.replant", true)
        val destroyLeaves by fc.cBoolean("$PATH.destroy_leaves", true)
    }

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
