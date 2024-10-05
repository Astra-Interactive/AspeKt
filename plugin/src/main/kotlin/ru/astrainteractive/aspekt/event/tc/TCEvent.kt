@file:OptIn(UnsafeApi::class)

package ru.astrainteractive.aspekt.event.tc

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.kotlin.tooling.core.UnsafeApi
import ru.astrainteractive.aspekt.event.tc.di.TCDependencies
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.astralibs.event.EventListener
import kotlin.random.Random

class TCEvent(
    dependencies: TCDependencies
) : TCDependencies by dependencies, EventListener {
    private val treeCapitatorConfig: PluginConfiguration.TreeCapitator
        get() = configuration.treeCapitator

    @Suppress("UnusedPrivateMember")
    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onBlockBreak(e: BlockBreakEvent) {
        val block = e.block
        val material = block.type
        val player = e.player
        val tool = player.inventory.itemInMainHand
        if (e.isCancelled) return
        if (!tool.type.name.contains("AXE", true)) return
        if (!player.isSneaking) return
        if (!treeCapitatorConfig.enabled) return
        if (!isLog(block.type)) return
        breakRecursively(player, block, 0, tool)
        if (treeCapitatorConfig.replant) {
            val sapling = saplingFromBlock(material) ?: return
            placeSapling(sapling, block, 0)
        }
    }

    private tailrec fun placeSapling(sapling: Material, block: Block, i: Int) {
        if (i >= treeCapitatorConfig.replantMaxIterations) return
        if (!isDirt(block.type)) {
            placeSapling(sapling, block.getRelative(BlockFace.DOWN), i + 1)
            return
        }
        val airBlock = block.getRelative(BlockFace.UP)
        if (airBlock.type != Material.AIR) {
            placeSapling(sapling, block.getRelative(BlockFace.DOWN), i + 1)
            return
        }
        scope.launch(dispatchers.BukkitAsync) {
            delay(100)
            withContext(dispatchers.BukkitMain) {
                airBlock.location.block.setType(sapling, true)
            }
        }
    }

    private fun breakRecursively(player: Player, block: Block, i: Int, tool: ItemStack) {
        if (i >= treeCapitatorConfig.destroyLimit) return
        val isLog = isLog(block.type)
        val isLeave = isLeaves(block.type)
        if (!isLog && !isLeave) return
        if (isLeave && treeCapitatorConfig.destroyLeaves) {
            block.breakNaturally()
        }
        if (isLog) {
            block.breakNaturally()
            damageItem(player, tool)
        }
        BlockFace.entries.forEach {
            breakRecursively(player, block.getRelative(it), i + 1, tool)
        }
    }

    private fun isDirt(mat: Material): Boolean {
        return mat == Material.GRASS_BLOCK ||
            mat == Material.DIRT ||
            mat == Material.ROOTED_DIRT ||
            mat == Material.COARSE_DIRT
    }

    /**
     * Checks if material is log or not
     */
    private fun isLog(mat: Material): Boolean {
        return mat.name.contains("STRIPPED_") ||
            mat.name.contains("_LOG") ||
            mat == Material.CRIMSON_STEM ||
            mat == Material.WARPED_STEM
    }

    /**
     * Damage axe item
     */
    private fun damageItem(player: Player, tool: ItemStack) {
        if (!treeCapitatorConfig.damageAxe) return
        val meta: ItemMeta = tool.itemMeta ?: return
        val damageable = meta as? Damageable ?: return
        val maxDmg: Short = tool.type.maxDurability
        var dmg: Int = damageable.damage
        // Check for durability enchantment
        val unbLevel: Int = tool.getEnchantmentLevel(Enchantment.UNBREAKING)
        if (Random.nextInt(unbLevel + 1) == 0) {
            damageable.damage = ++dmg
        }
        tool.itemMeta = damageable
        if (dmg < maxDmg) return
        if (treeCapitatorConfig.breakAxe) {
            tool.amount = 0
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
        } else {
            damageable.damage = maxDmg - 1
            tool.setItemMeta(damageable)
        }
    }

    /**
     * Checks if material is leave or not
     */
    private fun isLeaves(mat: Material): Boolean {
        return mat.name.contains("LEAVES") ||
            mat == Material.NETHER_WART_BLOCK ||
            mat == Material.WARPED_WART_BLOCK ||
            mat == Material.SHROOMLIGHT
    }

    /**
     * Converts WoodLog into it's sapling
     * @return sapling material or null if it's not found
     */
    private fun saplingFromBlock(material: Material): Material? {
        return when (material) {
            Material.OAK_LOG -> Material.OAK_SAPLING
            Material.DARK_OAK_LOG -> Material.DARK_OAK_SAPLING
            Material.SPRUCE_LOG -> Material.SPRUCE_SAPLING
            Material.ACACIA_LOG -> Material.ACACIA_SAPLING
            Material.AZALEA -> null
            Material.BIRCH_LOG -> Material.BIRCH_SAPLING
            Material.JUNGLE_LOG -> Material.JUNGLE_SAPLING
            Material.MANGROVE_LOG -> null
            else -> null
        }
    }
}
