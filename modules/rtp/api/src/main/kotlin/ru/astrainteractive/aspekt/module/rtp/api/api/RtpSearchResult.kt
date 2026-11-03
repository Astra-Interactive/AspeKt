package ru.astrainteractive.aspekt.module.rtp.api.api

import ru.astrainteractive.astralibs.server.location.KLocation

sealed interface RtpSearchResult {

    data class Success(val location: KLocation) : RtpSearchResult

    data object MaxRetriesReached : RtpSearchResult

    data object NotFound : RtpSearchResult
}
