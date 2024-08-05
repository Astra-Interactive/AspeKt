package ru.astrainteractive.aspekt.module.moneydrop

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.kotlin.com.google.common.cache.Cache
import org.jetbrains.kotlin.com.google.common.cache.CacheBuilder
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.persistence.Persistence.getPersistentData
import ru.astrainteractive.astralibs.persistence.Persistence.hasPersistentData
import ru.astrainteractive.astralibs.persistence.Persistence.setPersistentDataType
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.getValue
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MoneyDropController(
    pluginConfigurationDependency: Dependency<PluginConfiguration>,
    translationDependency: Dependency<PluginTranslation>,
    kyoriComponentSerializerDependency: Dependency<KyoriComponentSerializer>
) {
    private val pluginConfiguration by pluginConfigurationDependency
    private val translation by translationDependency
    private val kyoriComponentSerializer by kyoriComponentSerializerDependency

    private val dropCache: Cache<String, Unit> = CacheBuilder
        .newBuilder()
        .maximumSize(64)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build()

    private fun Location.toKeyLocation() = "${x.toInt()}${y.toInt()}${z.toInt()}"

    private fun checkForChance(entry: PluginConfiguration.MoneyDropEntry): Boolean {
        val chance = entry.chance
        return chance > Random.nextDouble(0.0, 100.0)
    }

    private fun drop(location: Location, entry: PluginConfiguration.MoneyDropEntry) {
        if (dropCache.getIfPresent(location.toKeyLocation()) != null) return
        dropCache.put(location.toKeyLocation(), Unit)

        val amount = Random.nextDouble(entry.min, entry.max)
        val material = Material.RAW_GOLD
        val itemStack = ItemStack(material)
        val name = translation.general.droppedMoney.let(kyoriComponentSerializer::toComponent)
        itemStack.editMeta {
            it.displayName(name)
            it.setPersistentDataType(MoneyDropFlag.Flag, true)
            it.setPersistentDataType(MoneyDropFlag.Amount, amount)
        }
        val item = location.world.dropItemNaturally(location, itemStack)
        item.customName(name)
        item.isCustomNameVisible = true
    }

    fun isMoneyDropItem(itemStack: ItemStack): Boolean {
        if (!itemStack.itemMeta.hasPersistentData(MoneyDropFlag.Flag)) return false
        return itemStack.itemMeta.getPersistentData(MoneyDropFlag.Flag) ?: false
    }

    fun getMoneyAmount(itemStack: ItemStack): Double? {
        return itemStack.itemMeta.getPersistentData(MoneyDropFlag.Amount)
    }

    fun tryDrop(location: Location, from: String) {
        pluginConfiguration.moneyDrop.values
            .filter { it.from == from }
            .filter(::checkForChance)
            .forEach { entry -> drop(location, entry) }
    }

    fun blockPlaced(location: Location) {
        dropCache.put(location.toKeyLocation(), Unit)
    }
}
