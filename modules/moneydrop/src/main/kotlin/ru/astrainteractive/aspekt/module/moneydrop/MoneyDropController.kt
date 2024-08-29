package ru.astrainteractive.aspekt.module.moneydrop

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.moneydrop.database.dao.MoneyDropDao
import ru.astrainteractive.aspekt.module.moneydrop.database.model.MoneyDropLocation
import ru.astrainteractive.aspekt.plugin.PluginConfiguration
import ru.astrainteractive.aspekt.plugin.PluginTranslation
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.persistence.Persistence.getPersistentData
import ru.astrainteractive.astralibs.persistence.Persistence.hasPersistentData
import ru.astrainteractive.astralibs.persistence.Persistence.setPersistentDataType
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.getValue
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import kotlin.random.Random

internal class MoneyDropController(
    pluginConfigurationDependency: Dependency<PluginConfiguration>,
    translationDependency: Dependency<PluginTranslation>,
    kyoriComponentSerializerDependency: Dependency<KyoriComponentSerializer>,
    private val dao: MoneyDropDao,
    private val dispatchers: KotlinDispatchers
) : AsyncComponent() {
    private val pluginConfiguration by pluginConfigurationDependency
    private val translation by translationDependency
    private val kyoriComponentSerializer by kyoriComponentSerializerDependency

    private fun Location.toMoneyDropLocation(additionalConstraint: String?) = MoneyDropLocation(
        x = this.x.toInt(),
        y = this.y.toInt(),
        z = this.z.toInt(),
        world = this.world.name,
        additionalConstraint = additionalConstraint
    )

    private fun checkForChance(entry: PluginConfiguration.MoneyDropEntry): Boolean {
        val chance = entry.chance
        return chance > Random.nextDouble(0.0, 100.0)
    }

    private suspend fun drop(location: Location, entry: PluginConfiguration.MoneyDropEntry) {
        if (dao.isLocationExists(location.toMoneyDropLocation(entry.from))) return
        rememberLocation(location, entry.from)

        val amount = Random.nextDouble(entry.min, entry.max)
        val material = Material.RAW_GOLD
        val itemStack = ItemStack(material)
        val name = translation.general.droppedMoney.let(kyoriComponentSerializer::toComponent)
        itemStack.editMeta {
            it.displayName(name)
            it.setPersistentDataType(MoneyDropFlag.Flag, true)
            it.setPersistentDataType(MoneyDropFlag.Amount, amount)
        }
        val item = withContext(dispatchers.Main) { location.world.dropItemNaturally(location, itemStack) }
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

    fun tryDrop(location: Location, from: String) = launch {
        pluginConfiguration.moneyDrop.values
            .filter { it.from == from }
            .filter(::checkForChance)
            .forEach { entry -> drop(location, entry) }
    }

    fun rememberLocation(location: Location, additionalConstraint: String? = null) {
        launch { dao.addLocation(location.toMoneyDropLocation(additionalConstraint)) }
    }
}
