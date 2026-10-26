package ru.astrainteractive.aspekt.module.moneydrop.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

object InstantSerializer : KSerializer<Instant> {
    private val mapSerializer = Long.serializer()

    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Instant) {
        return mapSerializer.serialize(encoder, value.toEpochMilliseconds())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val epochMilli = mapSerializer.deserialize(decoder)
        return Instant.fromEpochMilliseconds(epochMilli)
    }
}
