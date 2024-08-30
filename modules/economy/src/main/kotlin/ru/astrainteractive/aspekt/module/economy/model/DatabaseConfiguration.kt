package ru.astrainteractive.aspekt.module.economy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed class DatabaseConfiguration(val driver: String) {

    @SerialName("MySql")
    class MySql(
        val host: String,
        val port: Int,
        val user: String,
        val password: String,
        val name: String
    ) : DatabaseConfiguration("com.mysql.cj.jdbc.Driver")

    @SerialName("H2")
    data object H2 : DatabaseConfiguration("org.h2.Driver")
}
