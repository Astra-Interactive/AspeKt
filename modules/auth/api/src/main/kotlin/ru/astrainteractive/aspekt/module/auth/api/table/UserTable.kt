package ru.astrainteractive.aspekt.module.auth.api.table

import ru.astrainteractive.astralibs.exposed.table.StringIdTable

internal object UserTable : StringIdTable("USER_TABLE", "uuid") {
    val lastUsername = text("last_username")
    val passwordHash = text("password_hash")
}
