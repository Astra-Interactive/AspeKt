package ru.astrainteractive.aspekt.gui.entities.ui

import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.gui.entities.presentation.DefaultEntitiesComponent
import ru.astrainteractive.aspekt.gui.entities.presentation.EntitiesComponent
import ru.astrainteractive.aspekt.gui.entities.util.toMaterial
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.menu.editMeta
import ru.astrainteractive.astralibs.menu.menu.setIndex
import ru.astrainteractive.astralibs.menu.menu.setItemStack
import ru.astrainteractive.astralibs.menu.menu.setOnClickListener
import ru.astrainteractive.astralibs.serialization.KyoriComponentSerializer
import ru.astrainteractive.astralibs.string.StringDesc

class EntitiesGui(
    player: Player,
    private val bukkitDispatchers: BukkitDispatchers,
    kyoriComponentSerializer: KyoriComponentSerializer
) : PaginatedMenu(), KyoriComponentSerializer by kyoriComponentSerializer {
    private val viewModel by lazy {
        DefaultEntitiesComponent()
    }
    override val backPageButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(49)
            .setItemStack(ItemStack(Material.END_CRYSTAL))
            .editMeta {
                setDisplayName("Back")
            }
            .setOnClickListener {
                when (viewModel.model.value) {
                    is EntitiesComponent.Model.AllEntities -> inventory.close()
                    is EntitiesComponent.Model.ExactEntity -> {
                        viewModel.loadData()
                    }

                    EntitiesComponent.Model.Loading -> inventory.close()
                }
            }
            .build()

    override val nextPageButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(backPageButton.index + 1)
            .setItemStack(ItemStack(Material.PAPER))
            .editMeta {
                setDisplayName("Next")
            }
            .setOnClickListener { showPage(page + 1) }
            .build()

    override val prevPageButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(backPageButton.index + 1)
            .setItemStack(ItemStack(Material.PAPER))
            .editMeta {
                setDisplayName("Prev")
            }
            .setOnClickListener { showPage(page - 1) }
            .build()

    private val worldButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(backPageButton.index + 2)
            .setItemStack(ItemStack(Material.ENDER_EYE))
            .editMeta {
                val state = viewModel.model.value
                val world = (state as? EntitiesComponent.Model.AllEntities)?.world?.name
                setDisplayName("World: $world")
            }
            .setOnClickListener {
                viewModel.onWorldChangeClicked()
            }
            .build()

    private val filterButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(backPageButton.index - 2)
            .setItemStack(ItemStack(Material.ENDER_EYE))
            .editMeta {
                val state = viewModel.model.value
                val sort = (state as? EntitiesComponent.Model.AllEntities)?.sort
                setDisplayName("Sort: $sort")
            }
            .setOnClickListener { viewModel.onSortClicked() }
            .build()

    override val maxItemsAmount: Int
        get() = when (val state = viewModel.model.value) {
            is EntitiesComponent.Model.AllEntities -> state.list.size
            is EntitiesComponent.Model.ExactEntity -> state.list.size
            EntitiesComponent.Model.Loading -> 0
        }
    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: Component = StringDesc.Raw("Entities").let(kyoriComponentSerializer::toComponent)

    override var page: Int = 0
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override fun onCreated() {
        viewModel.model.onEach { render() }
            .flowOn(bukkitDispatchers.BukkitMain)
            .launchIn(menuScope)
        viewModel.loadData()
    }

    override fun render() {
        super.render()
        val state: EntitiesComponent.Model = viewModel.model.value
        inventory.clear()
        when (state) {
            is EntitiesComponent.Model.AllEntities -> {
                filterButton.setInventorySlot()
                worldButton.setInventorySlot()
                for (i in 0 until maxItemsPerPage) {
                    val index = maxItemsPerPage * page + i
                    val entity = state.list.getOrNull(index) ?: continue
                    InventorySlot.Builder()
                        .setIndex(i)
                        .setItemStack(ItemStack(entity.entityType.toMaterial()))
                        .editMeta {
                            setDisplayName("${entity.entityType.name}: ${entity.count}")
                        }
                        .setOnClickListener { viewModel.onEntityClicked(entity.entityType) }
                        .build()
                        .setInventorySlot()
                }
            }

            is EntitiesComponent.Model.ExactEntity -> {
                for (i in 0 until maxItemsPerPage) {
                    val index = maxItemsPerPage * page + i
                    val entity = state.list.getOrNull(index) ?: continue
                    InventorySlot.Builder()
                        .setIndex(i)
                        .setItemStack(ItemStack(entity.type.toMaterial()))
                        .editMeta {
                            val loc = entity.location
                            setDisplayName("(${loc.x.toInt()}; ${loc.y.toInt()}; ${loc.z.toInt()})")
                            if (playerHolder.player.location.world == loc.world) {
                                lore = listOf(
                                    "Distance: ${loc.distance(playerHolder.player.location).toInt()}"
                                )
                            }
                        }
                        .setOnClickListener { playerHolder.player.teleport(entity.location) }
                        .build()
                        .setInventorySlot()
                }
            }

            EntitiesComponent.Model.Loading -> Unit
        }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onPageChanged() {
        render()
    }
}
