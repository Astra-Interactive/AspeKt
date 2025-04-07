package ru.astrainteractive.aspekt.inventorysort.event.sort

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.klibs.mikro.core.util.next
import java.util.UUID

internal class SortController {

    private val sortTypes = mutableMapOf<UUID, Sort>()

    fun rememberPlayer(player: Player) {
        sortTypes[player.uniqueId] = Sort.values().first()
    }

    fun removePlayer(player: Player) {
        sortTypes.remove(player.uniqueId)
    }

    fun trySortInventory(clickedInventory: Inventory, player: Player) {
        val contents = clickedInventory.storageContents
        val sortType = sortTypes[player.uniqueId]?.next(Sort.values()) ?: Sort.values().first()
        sortTypes[player.uniqueId] = sortType
        clickedInventory.storageContents = when (sortType) {
            Sort.TYPE_ASC -> sortByType(contents, sortType.desc)
            Sort.TYPE_DESC -> sortByType(contents, sortType.desc)
            Sort.NAME_ASC -> sortByName(contents, sortType.desc)
            Sort.NAME_DESC -> sortByName(contents, sortType.desc)
            Sort.WOOL_ASC -> sortByWool(contents, sortType.desc)
            Sort.WOOL_DESC -> sortByWool(contents, sortType.desc)
            Sort.GLASS_ASC -> sortByGlass(contents, sortType.desc)
            Sort.GLASS_DESC -> sortByGlass(contents, sortType.desc)
            Sort.BLOCK_ASC -> sortByBlock(contents, sortType.desc)
            Sort.BLOCK_DESC -> sortByBlock(contents, sortType.desc)
            Sort.TOOL_ASC -> sortByTool(contents, sortType.desc)
            Sort.TOOLS_DESC -> sortByTool(contents, sortType.desc)
        }
    }

    private fun sortBy(
        content: Array<out ItemStack?>,
        desc: Boolean,
        algorithm: () -> Comparator<ItemStack?>,
    ): Array<out ItemStack?> = if (desc) {
        content.sortedArrayWith(algorithm())
    } else {
        content.sortedArrayWith(algorithm()).reversedArray()
    }

    private fun sortByType(content: Array<out ItemStack?>, desc: Boolean) = sortBy(content, desc) {
        compareBy { it?.type }
    }

    private fun sortByName(content: Array<out ItemStack?>, desc: Boolean) = sortBy(content, desc) {
        compareBy { it?.itemMeta?.displayName }
    }

    private fun sortByWool(content: Array<out ItemStack?>, desc: Boolean) = sortBy(content, desc) {
        compareBy {
            it?.type?.name?.contains("wool", ignoreCase = true)
        }
    }

    private fun sortByGlass(content: Array<out ItemStack?>, desc: Boolean) = sortBy(content, desc) {
        compareBy {
            it?.type?.name?.contains("glass", ignoreCase = true)
        }
    }

    private fun sortByBlock(content: Array<out ItemStack?>, desc: Boolean) = sortBy(content, desc) {
        compareBy(
            { it?.type?.isBlock },
            { it?.type?.name?.contains("glass", ignoreCase = true) },
            { it?.type?.name?.contains("wool", ignoreCase = true) },
            { it?.type?.name?.contains("wool", ignoreCase = true) },
            { it?.type?.name?.contains("plank", ignoreCase = true) },
            { it?.type?.name?.contains("wood", ignoreCase = true) },
            { it?.type?.name?.contains("ore", ignoreCase = true) },
            { it?.type?.name?.contains("block", ignoreCase = true) },
        )
    }

    private fun sortByTool(content: Array<out ItemStack?>, desc: Boolean) = sortBy(content, desc) {
        compareBy(
            { it?.type?.name?.contains("axe", ignoreCase = true) },
            { it?.type?.name?.contains("head", ignoreCase = true) },
            { it?.type?.name?.contains("leggings", ignoreCase = true) },
            { it?.type?.name?.contains("boots", ignoreCase = true) },
            { it?.type?.name?.contains("chestplate", ignoreCase = true) },
            { it?.type?.name?.contains("sword", ignoreCase = true) },
            { it?.type?.name?.contains("hoe", ignoreCase = true) },
        )
    }
}
