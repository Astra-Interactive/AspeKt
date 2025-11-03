package ru.astrainteractive.aspekt.module.jail.command.argumenttype

import ru.astrainteractive.astralibs.command.api.argumenttype.ArgumentConverter
import ru.astrainteractive.klibs.mikro.extensions.serialization.DurationSerializer
import kotlin.time.Duration

internal data object DurationArgumentType : ArgumentConverter<Duration> {
    // 1 year 2 month 3 weeks 4 days 5 hours 10 minutes 30 seconds
    // 3w4d6h10m30s
    override fun transform(argument: String): Duration {
        return DurationSerializer.toDuration(argument)
    }
}
