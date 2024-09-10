package ru.astrainteractive.aspekt.module.economy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
internal sealed interface DatabaseConfiguration {
    @Transient
    val driver: String

    @SerialName("MySql")
    @Serializable
    class MySql(
        val host: String,
        val port: Int,
        val user: String,
        val password: String,
        val name: String
    ) : DatabaseConfiguration {
        @Transient
        override val driver: String = "com.mysql.cj.jdbc.Driver"
    }

    @SerialName("H2")
    @Serializable
    data class H2(val name: String = "economy") : DatabaseConfiguration {
        @Transient
        override val driver: String = "org.h2.Driver"
    }
}
