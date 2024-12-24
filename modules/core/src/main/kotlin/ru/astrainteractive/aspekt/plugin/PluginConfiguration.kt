package ru.astrainteractive.aspekt.plugin

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
    val restrictions: Restrictions = Restrictions(),
    @SerialName("money_drop")
    val moneyDrop: Map<String, MoneyDropEntry> = emptyMap(),
    val towny: TownyConfiguration = TownyConfiguration(),
    @SerialName("advancement_money")
    val advancementMoney: AdvancementMoney = AdvancementMoney()
) {
    @Serializable
    data class AdvancementMoney(
        @SerialName("challenge")
        @YamlComment("Money given for challenge advancemetn")
        val challenge: Int = 5000,
        @SerialName("goal")
        @YamlComment("Money given long-road advancement")
        val goal: Int = 1000,
        @SerialName("task")
        @YamlComment("Money given for default task")
        val task: Int = 1000,
        @SerialName("currency_id")
        val currencyId: String? = null
    )

    @Serializable
    data class TownyConfiguration(
        @SerialName("leader_role_configuration")
        val leaderRoleConfiguration: LeaderRoleConfiguration = LeaderRoleConfiguration()
    ) {
        @Serializable
        data class LeaderRoleConfiguration(
            @SerialName("is_enabled")
            val isEnabled: Boolean = false,
            val initialDelay: Long = 0L,
            val delay: Long = 1.minutes.inWholeMilliseconds,
            val roleId: String = ""
        )
    }

    @Serializable
    data class MoneyDropEntry(
        val from: String,
        val chance: Double,
        val min: Double,
        val max: Double,
        val currencyId: String? = null
    )

    @Serializable
    data class Restrictions(
        @SerialName("explosion")
        val explosion: Explosion = Explosion(),
        @SerialName("place")
        val place: Place = Place(),
        @SerialName("spread")
        val spread: Spread = Spread()
    ) {
        @Serializable
        data class Explosion(
            @SerialName("damage_creeper")
            val creeperDamage: Boolean = true,
            @SerialName("damage_other")
            val otherDamage: Boolean = true,
            @SerialName("destroy")
            val destroy: Boolean = true
        )

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
    )

    @Serializable
    data class Announcements(
        @SerialName("interval")
        val interval: Long = 1000L,
        @SerialName("announcements")
        val announcements: Map<String, Announcement> = emptyMap()
    ) {
        @Serializable
        sealed interface Announcement {
            @Serializable
            @SerialName("TEXT")
            data class Text(
                val text: StringDesc.Raw,
            ) : Announcement

            @Serializable
            @SerialName("ACTION_BAR")
            data class ActionBar(
                val text: StringDesc.Raw,
            ) : Announcement

            @Serializable
            @SerialName("BOSS_BAR")
            data class BossBar(
                val text: StringDesc.Raw,
                val barColor: BarColor = BarColor.BLUE,
                @SerialName("duration_seconds")
                val durationSeconds: Long = 5,
            ) : Announcement {
                val duration: Duration
                    get() = durationSeconds.seconds

                enum class BarColor {
                    PINK,
                    BLUE,
                    RED,
                    GREEN,
                    YELLOW,
                    PURPLE,
                    WHITE
                }
            }
        }
    }

    @Serializable
    data class DiscordSRVLink(
        @SerialName("money_for_link")
        val moneyForLink: Int = 0,
        @SerialName("currency_id")
        val currencyId: String? = null,
        @SerialName("discord_linked_role")
        val discordLinkedRole: String? = null,
        @SerialName("minecraft_linked_role")
        val minecraftLinkedRole: String? = null,
        @SerialName("is_enabled")
        val isEnabled: Boolean = false,
        @SerialName("initial_delay")
        val initialDelay: Long = 0L,
        val delay: Long = 1.minutes.inWholeMilliseconds,
    )
}
