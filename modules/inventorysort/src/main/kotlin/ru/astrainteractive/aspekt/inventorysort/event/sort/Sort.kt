package ru.astrainteractive.aspekt.inventorysort.event.sort

enum class Sort(val desc: Boolean) {
    TYPE_ASC(false), TYPE_DESC(true),
    NAME_ASC(false), NAME_DESC(true),
    WOOL_ASC(false), WOOL_DESC(true),
    GLASS_ASC(false), GLASS_DESC(true),
    BLOCK_ASC(false), BLOCK_DESC(true),
    TOOL_ASC(false), TOOLS_DESC(true)
}
