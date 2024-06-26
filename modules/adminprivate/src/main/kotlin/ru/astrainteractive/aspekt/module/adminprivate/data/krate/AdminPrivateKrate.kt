package ru.astrainteractive.aspekt.module.adminprivate.data.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminPrivateConfig
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kstorage.suspend.StateFlowSuspendKrate
import ru.astrainteractive.klibs.kstorage.suspend.impl.DefaultSuspendMutableKrate
import java.io.File

internal class AdminPrivateKrate(
    file: File,
    stringFormat: StringFormat = YamlStringFormat()
) : StateFlowSuspendKrate.Mutable<AdminPrivateConfig> by DefaultSuspendMutableKrate(
    factory = { AdminPrivateConfig() },
    saver = { value -> stringFormat.writeIntoFile(value, file) },
    loader = { stringFormat.parse<AdminPrivateConfig>(file).onFailure(Throwable::printStackTrace).getOrNull() },
)
