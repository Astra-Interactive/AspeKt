package ru.astrainteractive.aspekt.module.moneydrop.database.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

object InstantSerializer : KSerializer<Instant> {
    private val mapSerializer = Long.serializer()

    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Instant) {
        return mapSerializer.serialize(encoder, value.toEpochMilli())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val epochMilli = mapSerializer.deserialize(decoder)
        return Instant.ofEpochMilli(epochMilli)
    }
}
