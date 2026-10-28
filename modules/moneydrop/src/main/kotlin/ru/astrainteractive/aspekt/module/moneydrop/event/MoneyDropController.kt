package ru.astrainteractive.aspekt.module.moneydrop.event

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.moneydrop.api.MoneyDropLocationRepository
import ru.astrainteractive.aspekt.module.moneydrop.model.MoneyDropConfiguration
import ru.astrainteractive.aspekt.module.moneydrop.model.MoneyDropLocation
import ru.astrainteractive.aspekt.plugin.model.PluginTranslation
import ru.astrainteractive.astralibs.coroutines.withTimings
import ru.astrainteractive.astralibs.persistence.Persistence.getPersistentDataOrNull
import ru.astrainteractive.astralibs.persistence.Persistence.hasPersistentData
import ru.astrainteractive.astralibs.persistence.Persistence.setPersistentDataType
import ru.astrainteractive.klibs.kstorage.api.CachedKrate
import ru.astrainteractive.klibs.kstorage.api.getValue
import ru.astrainteractive.klibs.mikro.core.coroutines.CoroutineFeature
import ru.astrainteractive.klibs.mikro.core.dispatchers.KotlinDispatchers
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Clock

internal class MoneyDropController(
    moneyDropKrate: CachedKrate<MoneyDropConfiguration>,
    translationDependency: CachedKrate<PluginTranslation>,
    private val repository: MoneyDropLocationRepository,
    private val dispatchers: KotlinDispatchers
) : CoroutineFeature by CoroutineFeature.Default(Dispatchers.IO).withTimings() {
    private val moneyDropConfig by moneyDropKrate
    private val translation by translationDependency

    private fun Location.toMoneyDropLocation(additionalConstraint: String?): MoneyDropLocation {
        return MoneyDropLocation(
            x = this.x.roundToInt(),
            y = this.y.roundToInt(),
            z = this.z.roundToInt(),
            world = this.world.name,
            additionalConstraint = additionalConstraint,
            instant = Clock.System.now()
        )
    }

    @Suppress("MagicNumber")
    private fun checkForChance(entry: MoneyDropConfiguration.MoneyDropEntry): Boolean {
        val chance = entry.chance
        return chance > Random.nextDouble(0.0, 100.0)
    }

    fun recordLocation(location: Location, additionalConstraint: String? = null) {
        launch { repository.addLocation(location.toMoneyDropLocation(additionalConstraint)) }
    }

    private suspend fun drop(location: Location, entry: MoneyDropConfiguration.MoneyDropEntry) {
        if (repository.isLocationExists(location.toMoneyDropLocation(entry.from))) return
        recordLocation(location, entry.from)

        val amount = Random.nextDouble(entry.min, entry.max)
        val material = Material.RAW_GOLD
        val itemStack = ItemStack(material)
        // Every nearby player sees the same dropped item, so its name is in the default language
        val name = translation.moneyDrop.itemName.toComponent(Locale.ROOT)
        itemStack.editMeta { meta ->
            meta.displayName(name)
            meta.setPersistentDataType(MoneyDropFlag.Flag, true)
            meta.setPersistentDataType(MoneyDropFlag.Amount, amount)
            entry.currencyId?.let { currencyId ->
                meta.setPersistentDataType(MoneyDropFlag.CurrencyId, currencyId)
            }
        }
        val item = withContext(dispatchers.Main) { location.world.dropItemNaturally(location, itemStack) }
        item.customName(name)
        item.isCustomNameVisible = true
    }

    fun isMoneyDropItem(itemStack: ItemStack): Boolean {
        if (!itemStack.itemMeta.hasPersistentData(MoneyDropFlag.Flag)) return false
        return itemStack.itemMeta.getPersistentDataOrNull(MoneyDropFlag.Flag) ?: false
    }

    fun findMoneyAmount(itemStack: ItemStack): Double? {
        return itemStack.itemMeta.getPersistentDataOrNull(MoneyDropFlag.Amount)
    }

    fun findMoneyCurrency(itemStack: ItemStack): String? {
        return itemStack.itemMeta.getPersistentDataOrNull(MoneyDropFlag.CurrencyId)
    }

    fun tryDrop(location: Location, from: String) {
        launch {
            moneyDropConfig.moneyDrop.values
                .filter { entry -> entry.from == from }
                .filter(::checkForChance)
                .forEach { entry -> drop(location, entry) }
        }
    }
}
