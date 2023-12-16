package ru.astrainteractive.aspekt.event.moneydrop

import org.bukkit.persistence.PersistentDataType
import ru.astrainteractive.astralibs.persistence.BukkitConstant

object MoneyDropFlag {
    /**
     * Amount of money will be added
     */
    data object Amount : BukkitConstant<Double, Double> by BukkitConstant(
        "esmp",
        "amount",
        PersistentDataType.DOUBLE
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
