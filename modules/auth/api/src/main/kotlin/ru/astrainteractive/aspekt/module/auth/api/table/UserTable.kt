package ru.astrainteractive.aspekt.module.auth.api.table

import org.jetbrains.exposed.sql.javatime.datetime
import ru.astrainteractive.astralibs.exposed.table.StringIdTable

internal object UserTable : StringIdTable("USER_TABLE", "uuid") {
    val lastUsername = text("last_username")
    val passwordHash = text("password_hash")
    val lastIpAddress = text("last_ip_address")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}
