package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginConfiguration(
    @SerialName("sit")
    val sit: Boolean = true,
    @SerialName("discordsrv")
    val discordSRVLink: DiscordSRVLink = DiscordSRVLink(),
    @SerialName("announcements")
    val announcements: Announcements = Announcements(),
    @SerialName("auto_crop")
    val autoCrop: AutoCrop = AutoCrop(),
    @SerialName("tree_capitator")
    val treeCapitator: TreeCapitator = TreeCapitator(),
    @SerialName("restrictions")
    val restrictions: Restrictions = Restrictions()
) {

    @Serializable
    data class Restrictions(
        @SerialName("explode")
        val explode: Boolean = true,
        @SerialName("place:")
        val place: Place = Place(),
        @SerialName("spread")
        val spread: Spread = Spread()
    ) {
        @Serializable
        data class Place(
            @SerialName("tnt")
            val tnt: Boolean = true,
            @SerialName("lava")
            val lava: Boolean = true,
        )

        @Serializable
        data class Spread(
            @SerialName("lava")
            val lava: Boolean = true,
            @SerialName("fire")
            val fire: Boolean = true
        )
    }

    @Serializable
    @Suppress("LongParameterList")
    data class TreeCapitator(
        @SerialName("enabled")
        val enabled: Boolean = true,
        @SerialName("destroy_limit")
        val destroyLimit: Int = 16,
        @SerialName("damage_axe")
        val damageAxe: Boolean = true,
        @SerialName("break_axe")
        val breakAxe: Boolean = true,
        @SerialName("replant")
        val replant: Boolean = true,
        @SerialName("replant_max_iterations")
        val replantMaxIterations: Int = 16,
        @SerialName("destroy_leaves")
        val destroyLeaves: Boolean = true
    )

    @Serializable
    data class AutoCrop(
        @SerialName("enabled")
        val enabled: Boolean = true,
        @SerialName("min")
        val min: Int = 0,
        @SerialName("max")
        val max: Int = 0,
        @SerialName("duping")
        val dupeProtection: DupeProtection = DupeProtection()
    ) {
        @Serializable
        data class DupeProtection(
            @SerialName("enabled")
            val enabled: Boolean = true,
            @SerialName("clear_every")
            val clearEveryMs: Long = 60_000L,
            @SerialName("location_timeout")
            val locationTimeoutMs: Long = 15_000L
        )
    }

    @Serializable
    data class Announcements(
        @SerialName("interval")
        val interval: Long = 1000L,
        @SerialName("announcements")
        val announcements: List<String> = emptyList()
    )

    @Serializable
    data class DiscordSRVLink(
        @SerialName("money_for_link")
        val moneyForLink: Int = 0,
        @SerialName("on_linked")
        val onLinked: LinkConfiguration = LinkConfiguration(),
        @SerialName("on_unlinked")
        val onUnlinked: LinkConfiguration = LinkConfiguration()
    ) {
        @Serializable
        data class LinkConfiguration(
            @SerialName("discord")
            val discord: RoleConfiguration = RoleConfiguration(),
            @SerialName("luckperms")
            val luckPerms: RoleConfiguration = RoleConfiguration()
        ) {
            @Serializable
            data class RoleConfiguration(
                @SerialName("add_roles")
                val addRoles: List<String> = emptyList(),
                @SerialName("remove_roles")
                val removeRoles: List<String> = emptyList()
            )
        }
    }
}
