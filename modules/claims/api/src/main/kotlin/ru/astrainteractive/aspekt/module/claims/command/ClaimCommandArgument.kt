package ru.astrainteractive.aspekt.module.claims.command

internal enum class ClaimCommandArgument(val value: String) {
    CLAIM("claim"),
    UNCLAIM("unclaim"),
    MAP("map"),
    FLAG("flag"),
    ADD_MEMBER("add"),
    REMOVE_MEMBER("remove"),
}
