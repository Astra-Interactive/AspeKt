package ru.astrainteractive.aspekt.module.claims.command.claim

import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgument

enum class ClaimCommandArgument(override val value: String) : EnumArgument {
    CLAIM("claim"),
    UNCLAIM("unclaim"),
    MAP("map"),
    FLAG("flag"),
    ADD_MEMBER("add"),
    REMOVE_MEMBER("remove"),
}
