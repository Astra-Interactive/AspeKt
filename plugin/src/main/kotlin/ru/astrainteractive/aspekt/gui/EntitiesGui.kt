package ru.astrainteractive.aspekt.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import ru.astrainteractive.aspekt.gui.store.EntitiesState
import ru.astrainteractive.astralibs.menu.holder.DefaultPlayerHolder
import ru.astrainteractive.astralibs.menu.holder.PlayerHolder
import ru.astrainteractive.astralibs.menu.menu.PaginatedMenu
import ru.astrainteractive.astralibs.menu.utils.InventoryButton
import ru.astrainteractive.astralibs.menu.utils.ItemStackButtonBuilder
import ru.astrainteractive.astralibs.menu.utils.MenuSize
import ru.astrainteractive.astralibs.menu.utils.click.MenuClickListener

class EntitiesGui(player: Player) : PaginatedMenu() {
    private val clickListener = MenuClickListener()
    private val viewModel by lazy {
        EntitiesViewModel()
    }
    override val backPageButton: InventoryButton = ItemStackButtonBuilder {
        this.index = 49
        this.itemStack {
            this.type = Material.END_CRYSTAL
            this.editMeta {
                it.setDisplayName("Back")
            }
        }
        this.onClick = {
            when(viewModel.state.value){
                is EntitiesState.AllEntities -> inventory.close()
                is EntitiesState.ExactEntity -> {
                    viewModel.loadData()
                }
                EntitiesState.Loading -> inventory.close()
            }
        }
    }
    override val nextPageButton: InventoryButton = ItemStackButtonBuilder {
        this.index = backPageButton.index+1
        this.itemStack {
            this.type = Material.PAPER
            this.editMeta {
                it.setDisplayName("Next")
            }
        }
        this.onClick = {
            showPage(page - 1)
        }
    }
    override val prevPageButton: InventoryButton = ItemStackButtonBuilder {
        this.index = backPageButton.index-1
        this.itemStack {
            this.type = Material.PAPER
            this.editMeta {
                it.setDisplayName("Prev")
            }
        }
        this.onClick = {
            showPage(page - 1)
        }
    }
    private val worldButton: InventoryButton
        get() = ItemStackButtonBuilder {
            val state = viewModel.state.value
            val world = (state as? EntitiesState.AllEntities)?.world?.name
            this.index = backPageButton.index+2
            this.itemStack {
                this.type = Material.ENDER_EYE
                this.editMeta {
                    it.setDisplayName("World: $world")
                }
            }
            this.onClick = {
                viewModel.onWorldChangeClicked()
            }
        }
    private val filterButton: InventoryButton
        get() = ItemStackButtonBuilder {
            val state = viewModel.state.value
            val sort = (state as? EntitiesState.AllEntities)?.sort
            this.index = backPageButton.index-2
            this.itemStack {
                this.type = Material.ENDER_EYE
                this.editMeta {
                    it.setDisplayName("Sort: $sort")
                }
            }
            this.onClick = {
                viewModel.onSortClicked()
            }
        }
    override val maxItemsAmount: Int
        get() = when (val state = viewModel.state.value) {
            is EntitiesState.AllEntities -> state.list.size
            is EntitiesState.ExactEntity -> state.list.size
            EntitiesState.Loading -> 0
        }
    override val menuSize: MenuSize = MenuSize.XL
    override var menuTitle: String = "Entities"

    override var page: Int = 0
    override val playerHolder: PlayerHolder = DefaultPlayerHolder(player)


    override fun onCreated() {
        viewModel.state.collectOn(block = ::renderPage)
        viewModel.loadData()
    }

    private fun renderPage(state: EntitiesState = viewModel.state.value) {
        clickListener.clearClickListener()
        inventory.clear()
        when (state) {
            is EntitiesState.AllEntities -> {
                filterButton.also(clickListener::remember).setInventoryButton()
                worldButton.also(clickListener::remember).setInventoryButton()
                for (i in 0 until maxItemsPerPage) {
                    val index = maxItemsPerPage * page + i
                    val entity = state.list.getOrNull(index) ?: continue
                    ItemStackButtonBuilder {
                        this.index = i
                        this.itemStack {
                            this.type = entity.entityType.toMaterial()
                            this.editMeta {
                                it.setDisplayName("${entity.entityType.name}: ${entity.count}")
                            }
                        }
                        this.onClick = {
                            viewModel.onEntityClicked(entity.entityType)
                        }
                    }.also(clickListener::remember).setInventoryButton()
                }
            }

            is EntitiesState.ExactEntity -> {
                for (i in 0 until maxItemsPerPage) {
                    val index = maxItemsPerPage * page + i
                    val entity = state.list.getOrNull(index) ?: continue
                    ItemStackButtonBuilder {
                        this.index = i
                        this.itemStack {
                            this.type = entity.type.toMaterial()
                            val loc = entity.location
                            this.editMeta {
                                it.setDisplayName("(${loc.x.toInt()}; ${loc.y.toInt()}; ${loc.z.toInt()})")
                                if (playerHolder.player.location.world == loc.world)
                                    it.lore = listOf(
                                        "Distance: ${loc.distance(playerHolder.player.location).toInt()}"
                                    )
                            }
                        }
                        this.onClick = {
                            playerHolder.player.teleport(entity.location)
                        }
                    }.also(clickListener::remember).setInventoryButton()
                }
            }

            EntitiesState.Loading -> Unit
        }
        setManageButtons(clickListener)
    }

    override fun onInventoryClicked(e: InventoryClickEvent) {
        e.isCancelled = true
        clickListener.onClick(e)
    }

    override fun onInventoryClose(it: InventoryCloseEvent) {
        close()
    }

    override fun onPageChanged() {
        renderPage()
    }
}