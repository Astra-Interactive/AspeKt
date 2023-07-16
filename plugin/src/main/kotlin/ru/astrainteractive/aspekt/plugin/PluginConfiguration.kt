package ru.astrainteractive.aspekt.plugin

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.aspekt.util.cBoolean
import ru.astrainteractive.aspekt.util.cInt
import ru.astrainteractive.aspekt.util.cStringList
import ru.astrainteractive.aspekt.util.getValue

class PluginConfiguration(private val fc: FileConfiguration) {
    val sit by fc.cBoolean("core.sit", true)
    val discordSRVLink = DiscordSRVLink()
    val announcements = Announcements()
    val autoCrop = AutoCrop()
    val tc = TC()
    val restrictions = Restrictions()

    inner class Restrictions {
        @Suppress("VariableNaming")
        private val PATH: String = "core.restrictions"
        val placeTnt by fc.cBoolean("$PATH.place.tnt", false)
        val explode by fc.cBoolean("$PATH.explode", false)
        val placeLava by fc.cBoolean("$PATH.place.lava", false)
        val spreadLava by fc.cBoolean("$PATH.spread.lava", false)
        val spreadFire by fc.cBoolean("$PATH.spread.fire", false)
    }

    inner class TC {
        @Suppress("VariableNaming")
        private val PATH: String = "core.tree_capitator"

        val enabled by fc.cBoolean("$PATH.enabled", true)
        val destroyLimit by fc.cInt("$PATH.destroy_limit", 16)
        val damageAxe by fc.cBoolean("$PATH.damage_axe", true)
        val breakAxe by fc.cBoolean("$PATH.break_axe", true)
        val replant by fc.cBoolean("$PATH.replant", true)
        val replantMaxIterations by fc.cInt("$PATH.replant_max_iterations", 16)
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

    @Suppress("MemberNameEqualsClassName")
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
