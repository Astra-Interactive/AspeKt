package ru.astrainteractive.aspekt.module.jail.usecase

import ru.astrainteractive.aspekt.module.jail.model.Jail
import ru.astrainteractive.aspekt.module.jail.model.JailInmate
import ru.astrainteractive.aspekt.module.jail.model.JailLocation
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

internal object JailTestData {
    val inmateUuid: UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
    val jail = Jail(name = "cell", location = JailLocation(world = "world", x = 1.0, y = 2.0, z = 3.0))
    val inmate = JailInmate(
        uuid = inmateUuid.toString(),
        lastUsername = "Prisoner",
        jailName = "cell",
        duration = 10.minutes,
        start = Instant.ofEpochSecond(0),
        lastLocation = JailLocation(world = "world", x = 10.0, y = 20.0, z = 30.0)
    )
}
