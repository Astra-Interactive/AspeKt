package ru.astrainteractive.aspekt.gui.entities.ui

import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import ru.astrainteractive.aspekt.gui.entities.presentation.DefaultEntitiesComponent
import ru.astrainteractive.aspekt.gui.entities.presentation.EntitiesComponent
import ru.astrainteractive.aspekt.gui.entities.util.toMaterial
import ru.astrainteractive.astralibs.async.BukkitDispatchers
import ru.astrainteractive.astralibs.menu.clicker.Click
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.InventorySlot
import ru.astrainteractive.astralibs.menu.menu.MenuSize
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astralibs.string.StringDesc

class EntitiesGui(
    player: Player,
    private val bukkitDispatchers: BukkitDispatchers,
    translationContext: BukkitTranslationContext
) : PaginatedMenu(),
    BukkitTranslationContext by translationContext {
    private val viewModel by lazy {
        DefaultEntitiesComponent()
    }
    override val backPageButton: InventorySlot = InventorySlot.Builder {
        this.index = 49
        this.itemStack = ItemStack(Material.END_CRYSTAL).apply {
            this.editMeta {
                it.setDisplayName("Back")
            }
        }
        this.click = Click {
            when (viewModel.model.value) {
                is EntitiesComponent.Model.AllEntities -> inventory.close()
                is EntitiesComponent.Model.ExactEntity -> {
                    viewModel.loadData()
                }

                EntitiesComponent.Model.Loading -> inventory.close()
            }
        }
    }
    override val nextPageButton: InventorySlot = InventorySlot.Builder {
        this.index = backPageButton.index + 1
        this.itemStack = ItemStack(Material.PAPER).apply {
            this.editMeta {
                it.setDisplayName("Next")
            }
        }
        this.click = Click {
            showPage(page - 1)
        }
    }
    override val prevPageButton: InventorySlot = InventorySlot.Builder {
        this.index = backPageButton.index - 1
        this.itemStack = ItemStack(Material.PAPER).apply {
            this.editMeta {
                it.setDisplayName("Prev")
            }
        }
        this.click = Click {
            showPage(page - 1)
        }
    }
    private val worldButton: InventorySlot
        get() = InventorySlot.Builder {
            val state = viewModel.model.value
            val world = (state as? EntitiesComponent.Model.AllEntities)?.world?.name
            this.index = backPageButton.index + 2
            this.itemStack = ItemStack(Material.ENDER_EYE).apply {
                this.editMeta {
                    it.setDisplayName("World: $world")
                }
            }
            this.click = Click {
                viewModel.onWorldChangeClicked()
            }
        }
    private val filterButton: InventorySlot
        get() = InventorySlot.Builder {
            val state = viewModel.model.value
            val sort = (state as? EntitiesComponent.Model.AllEntities)?.sort
            this.index = backPageButton.index - 2
            this.itemStack = ItemStack(Material.ENDER_EYE).apply {
                this.editMeta {
                    it.setDisplayName("Sort: $sort")
                }
            }
            this.click = Click {
                viewModel.onSortClicked()
            }
        }
    override val maxItemsAmount: Int
        get() = when (val state = viewModel.model.value) {
            is EntitiesComponent.Model.AllEntities -> state.list.size
            is EntitiesComponent.Model.ExactEntity -> state.list.size
            EntitiesComponent.Model.Loading -> 0
        }
    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: Component = StringDesc.Raw("Entities").toComponent()

    override var page: Int = 0
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)

    override fun onCreated() {
        viewModel.model.onEach { render() }
            .flowOn(bukkitDispatchers.BukkitMain)
            .launchIn(componentScope)
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
                    InventorySlot.Builder {
                        this.index = i
                        this.itemStack = ItemStack(entity.entityType.toMaterial()).apply {
                            this.editMeta {
                                it.setDisplayName("${entity.entityType.name}: ${entity.count}")
                            }
                        }
                        this.click = Click {
                            viewModel.onEntityClicked(entity.entityType)
                        }
                    }.setInventorySlot()
                }
            }

            is EntitiesComponent.Model.ExactEntity -> {
                for (i in 0 until maxItemsPerPage) {
                    val index = maxItemsPerPage * page + i
                    val entity = state.list.getOrNull(index) ?: continue
                    InventorySlot.Builder {
                        this.index = i
                        this.itemStack = ItemStack(entity.type.toMaterial()).apply {
                            val loc = entity.location
                            this.editMeta {
                                it.setDisplayName("(${loc.x.toInt()}; ${loc.y.toInt()}; ${loc.z.toInt()})")
                                if (playerHolder.player.location.world == loc.world) {
                                    it.lore = listOf(
                                        "Distance: ${loc.distance(playerHolder.player.location).toInt()}"
                                    )
                                }
                            }
                        }
                        this.click = Click {
                            playerHolder.player.teleport(entity.location)
                        }
                    }.setInventorySlot()
                }
            }

            EntitiesComponent.Model.Loading -> Unit
        }
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        super.onInventoryClicked(e)
        e.isCancelled = true
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        close()
    }

    override fun onPageChanged() {
        render()
    }
}
