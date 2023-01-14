package ru.astrainteractive.astraessentials.utils

import org.bukkit.configuration.file.FileConfiguration
import ru.astrainteractive.astralibs.configuration.getValue


class PluginConfiguration(fc: FileConfiguration) {
    val sit by fc.cBoolean("core.sit", true)
    val discordSRVLink = DiscordSRVLink(fc)
    val announcements = Announcements(fc)

    inner class Announcements(fc: FileConfiguration) {
        val interval = fc.cInt("announcements.interval", 10)
        val announcements = fc.cStringList("announcements.announcements")
    }

    inner class DiscordSRVLink(fc: FileConfiguration) {
        val discordOnLinked = Roles(fc, "on_linked.discord")
        val luckPermsOnLinked = Roles(fc, "on_linked.luckperms")
        val discordOnUnLinked = Roles(fc, "on_unlinked.discord")
        val luckPermsOnUnLinked = Roles(fc, "on_unlinked.luckperms")

        inner class Roles(fc: FileConfiguration, section: String) {
            val addRoles = fc.cStringList("discordsrv.$section.add_roles")
            val removeRoles = fc.cStringList("discordsrv.$section.remove_roles")
        }
    }
}

