package ru.astrainteractive.aspekt.module.rtp.api

import ru.astrainteractive.astralibs.server.location.KLocation

/**
 * Explicit outcome of a safe-location search, so the command layer can react to
 * each failure path with a distinct message instead of inspecting a nullable result.
 */
sealed interface RtpSearchResult {

    /** A safe location was found. */
    data class Success(val location: KLocation) : RtpSearchResult

    /** The search exhausted its configured retry budget without finding a spot. */
    data object MaxRetriesReached : RtpSearchResult

    /** The search could not be started (e.g. the player is no longer online). */
    data object NotFound : RtpSearchResult
}
