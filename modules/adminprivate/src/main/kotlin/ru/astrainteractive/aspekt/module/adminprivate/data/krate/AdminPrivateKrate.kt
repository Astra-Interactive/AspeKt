package ru.astrainteractive.aspekt.module.adminprivate.data.krate

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.module.adminprivate.model.AdminPrivateConfig
import ru.astrainteractive.astralibs.serialization.StringFormatExt.parse
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat
import ru.astrainteractive.klibs.kstorage.api.MutableKrate
import ru.astrainteractive.klibs.kstorage.api.impl.DefaultMutableKrate
import java.io.File

internal class AdminPrivateKrate(
    file: File,
    stringFormat: StringFormat = YamlStringFormat()
) : MutableKrate<AdminPrivateConfig> by DefaultMutableKrate(
    factory = { AdminPrivateConfig() },
    saver = { value -> stringFormat.writeIntoFile(value, file) },
    loader = { stringFormat.parse<AdminPrivateConfig>(file).getOrNull() },
    requireInstantLoading = false
)
