package ru.astrainteractive.aspekt.module.jail.command.jail.model

import ru.astrainteractive.astralibs.command.api.argumenttype.EnumArgument

internal enum class JailArg(override val value: String) : EnumArgument {
    LIST("list"),
    CREATE("create"),
    DELETE("delete"),
    INMATE("inmate"),
    FREE("free")
}
