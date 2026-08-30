package ru.astrainteractive.aspekt.feature.flagreader

import kotlinx.serialization.StringFormat
import ru.astrainteractive.aspekt.feature.model.FeatureFlag
import ru.astrainteractive.astralibs.util.parseOrDefault
import ru.astrainteractive.astralibs.util.writeIntoFile
import java.io.File

/**
 * Reads only the `is_enabled` projection of a module's config file; other keys
 * are ignored, so the same file can hold the module's full configuration.
 *
 * Never rewrites an existing file. A missing file is materialized once with
 * `is_enabled: false`, so the user always has a file to edit.
 */
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
