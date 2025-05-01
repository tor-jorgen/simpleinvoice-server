package org.simpleinvoice.server.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

class InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant = parseIso8601ToInstant(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeString(value.toString())
    }

    private fun parseIso8601ToInstant(input: String): Instant {
        val t =
            if (!input.contains("T")) {
                "${input}T00:00:00Z" // Append time if not present
            } else {
                input
            }
        return Instant.parse(t) // Default ISO-8601 parsing
//        return DateTimeFormatter.ISO_DATE.parse<Instant?>(
//            input,
//            TemporalQuery { temporal: TemporalAccessor? -> Instant.from(temporal) },
//        )!!
    }
}
