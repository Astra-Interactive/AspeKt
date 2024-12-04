package ru.astrainteractive.aspekt.module.moneydrop

import org.bukkit.persistence.PersistentDataType
import ru.astrainteractive.astralibs.persistence.BukkitConstant

internal object MoneyDropFlag {
    /**
     * Amount of money will be added
     */
    data object Amount : BukkitConstant<Double, Double> by BukkitConstant(
        "esmp",
        "amount",
        PersistentDataType.DOUBLE
    )

    data object CurrencyId : BukkitConstant<String, String> by BukkitConstant(
        "esmp",
        "currency_id",
        PersistentDataType.STRING
    )

    /**
     * Flag which declares item as MoneyDropItem
     */
    data object Flag : BukkitConstant<Byte, Boolean> by BukkitConstant(
        "esmp",
        "flag",
        PersistentDataType.BOOLEAN
    )
}
