package ru.astrainteractive.aspekt.module.entities.gui.entities.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.module.entities.gui.entities.presentation.DefaultEntitiesComponent
import ru.astrainteractive.aspekt.module.entities.gui.entities.presentation.EntitiesComponent
import ru.astrainteractive.aspekt.module.entities.gui.entities.util.toMaterial
import ru.astrainteractive.aspekt.util.getValue
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.kyori.KyoriComponentSerializer
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.inventory.PaginatedInventoryMenu
import ru.astrainteractive.astralibs.menu.inventory.model.InventorySize
import ru.astrainteractive.astralibs.menu.inventory.model.PageContext
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showNextPage
import ru.astrainteractive.astralibs.menu.inventory.util.PaginatedInventoryMenuExt.showPrevPage
import ru.astrainteractive.astralibs.menu.slot.InventorySlot
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.editMeta
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setIndex
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setItemStack
import ru.astrainteractive.astralibs.menu.slot.util.InventorySlotBuilderExt.setOnClickListener
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.klibs.kstorage.api.Krate

class EntitiesGui(
    player: Player,
    private val bukkitDispatchers: BukkitDispatchers,
    kyoriComponentSerializerKrate: Krate<KyoriComponentSerializer>
) : PaginatedInventoryMenu() {
    private val kyoriComponentSerializer by kyoriComponentSerializerKrate
    private val viewModel = DefaultEntitiesComponent()

    override val childComponents: List<CoroutineScope>
        get() = listOf(viewModel)

    private val backPageButton: InventorySlot
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
            .setOnClickListener { showNextPage() }
            .build()

    override val prevPageButton: InventorySlot
        get() = InventorySlot.Builder()
            .setIndex(backPageButton.index + 1)
            .setItemStack(ItemStack(Material.PAPER))
            .editMeta {
                setDisplayName("Prev")
            }
            .setOnClickListener { showPrevPage() }
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

    override val inventorySize: InventorySize = InventorySize.XL

    override var title: Component = with(kyoriComponentSerializer) {
        StringDesc.Raw("Entities").component
    }

    override var pageContext: PageContext = PageContext(
        page = 0,
        maxItems = 0,
        maxItemsPerPage = inventorySize.size - InventorySize.XXS.size
    )

    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override fun onInventoryCreated() {
        viewModel.model
            .onEach {
                pageContext = pageContext.copy(
                    maxItems = when (val state = viewModel.model.value) {
                        is EntitiesComponent.Model.AllEntities -> state.list.size
                        is EntitiesComponent.Model.ExactEntity -> state.list.size
                        EntitiesComponent.Model.Loading -> 0
                    }
                )
            }
            .onEach { render() }
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
                for (i in 0 until pageContext.maxItemsPerPage) {
                    val index = pageContext.maxItemsPerPage * pageContext.page + i
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
                for (i in 0 until pageContext.maxItemsPerPage) {
                    val index = pageContext.maxItemsPerPage * pageContext.page + i
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
}
