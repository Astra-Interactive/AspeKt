package ru.astrainteractive.astraessentials.events.sort

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.astralibs.utils.next
import java.util.*

class SortController {


    private val sortTypes = mutableMapOf<UUID, Sort>()
    fun rememberPlayer(player: Player) {
        sortTypes[player.uniqueId] = Sort.TYPE
    }

    fun removePlayer(player: Player) {
        sortTypes.remove(player.uniqueId)
    }

    fun trySortInventory(clickedInventory: Inventory, player: Player) {
        val contents = clickedInventory.storageContents ?: return
        val prevSortType = sortTypes[player.uniqueId] ?: Sort.TYPE
        sortTypes[player.uniqueId] =
            if (!prevSortType.desc) prevSortType.apply { desc = !desc } else prevSortType.next()
        clickedInventory.storageContents = when (prevSortType) {
            Sort.TYPE -> sortByType(contents, prevSortType.desc)
            Sort.NAME -> sortByName(contents, prevSortType.desc)
            Sort.WOOL -> sortByWool(contents, prevSortType.desc)
            Sort.GLASS -> sortByGlass(contents, prevSortType.desc)
            Sort.BLOCK -> sortByBlock(contents, prevSortType.desc)
            Sort.TOOL -> sortByTool(contents, prevSortType.desc)
        }.reversedArray()
    }

    private fun sortBy(
        content: Array<out ItemStack?>,
        desc: Boolean,
        algorithm: () -> Comparator<ItemStack?>,
    ): Array<out ItemStack?> {
        return if (desc) content.sortedArrayWith(algorithm())
        else content.sortedArrayWith(algorithm()).reversedArray()
    }

    private fun sortByType(content: Array<out ItemStack?>, desc: Boolean) =
        sortBy(content, desc) {
            compareBy<ItemStack?> { it?.type }
        }

    private fun sortByName(content: Array<out ItemStack?>, desc: Boolean) =
        sortBy(content, desc) {
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

    object ToolsComparable {
        private fun ItemStack.typeContains(it: String): Boolean? = this.type.name.contains(it, ignoreCase = true)
        val axe: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_axe") }
        val pickaxe: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("pickaxe") }
        val hoe: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_hoe") }
        val sword: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_sword") }
        val shovel: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_shovel") }

    }

    object BlocksComparable {
        private fun ItemStack.typeContains(it: String): Boolean? = this.type.name.contains(it, ignoreCase = true)
        val ore: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_ore") }
        val planks: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("plank") }
        val slab: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_slab") }
        val fence: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_fence") }
        val glass: (ItemStack?) -> Comparable<*>?
            get() = { it?.typeContains("_glass") }

    }

}