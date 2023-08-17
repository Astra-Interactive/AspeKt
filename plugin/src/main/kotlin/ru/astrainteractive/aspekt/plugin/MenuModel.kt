package ru.astrainteractive.aspekt.plugin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.menu.menu.MenuSize

@Serializable
data class MenuModel(
    val size: MenuSize,
    val command: String,
    val title: String,
    val items: Map<String, MenuItem> = emptyMap()
) {
    @Serializable
    data class MenuItem(
        val permission: String? = null,
        val index: Int,
        val name: String,
        val lore: List<String> = emptyList(),
        val amount: Int = 1,
        val material: String,
        @SerialName("custom_model_data")
        val customModelData: Int = 0,
        val price: Price = Price.Nothing,
        val reward: Reward = Reward.Nothing
    )

    @Serializable
    sealed interface Price {
        @Serializable
        @SerialName("money")
        data class Money(val amount: Double) : Price

        @Serializable
        @SerialName("nothing")
        data object Nothing : Price
    }

    @Serializable
    sealed interface Reward {
        @Serializable
        @SerialName("player_command")
        class PlayerCommands(val commands: List<String>) : Reward

        @Serializable
        @SerialName("console_command")
        class ConsoleCommands(val commands: List<String>) : Reward

        @Serializable
        @SerialName("nothing")
        data object Nothing : Reward
    }
}
