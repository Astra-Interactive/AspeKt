package ru.astrainteractive.aspekt.module.rtp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for the random-teleport feature, persisted to `rtp.yml`.
 *
 * All values are defaulted so the file can be generated from scratch.
 */
@Serializable
@Suppress("MagicNumber")
data class RtpConfig(
    @SerialName("min_x")
    val minX: Int = -100_000,
    @SerialName("max_x")
    val maxX: Int = 100_000,
    @SerialName("min_z")
    val minZ: Int = -100_000,
    @SerialName("max_z")
    val maxZ: Int = 100_000,
    @SerialName("min_y")
    val minY: Int = 30,
    @SerialName("max_y")
    val maxY: Int = 100,
    @SerialName("max_search_jobs")
    val maxSearchJobs: Int = 1,
    @SerialName("max_retry_count")
    val maxRetryCount: Int = 32,
    @SerialName("hazard_namespaces")
    val hazardNamespaces: List<String> = listOf(
        "mowziesmobs",
        "cataclysm",
        "bosses_of_mass_destruction",
        "conjurer_illager",
        "brutalbosses",
        "create",
    ),
)
