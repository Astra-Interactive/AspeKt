@file:Suppress("ImplementationVisibility")

package ru.astrainteractive.aspekt.feature.file

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.feature.api.FeatureFlagReader
import ru.astrainteractive.aspekt.feature.model.FeatureFlag
import ru.astrainteractive.astralibs.util.parseOrDefault
import ru.astrainteractive.astralibs.util.writeIntoFile
import java.io.File

class FileFeatureFlagReader(
    private val yamlFormat: StringFormat,
    private val file: File,
) : FeatureFlagReader {
    override fun read(): FeatureFlag {
        if (!file.exists()) {
            yamlFormat.writeIntoFile(FeatureFlag.DISABLED, file)
            return FeatureFlag.DISABLED
        }
        return yamlFormat.parseOrDefault(
            file = file,
            factory = { FeatureFlag.DISABLED }
        )
    }
}
