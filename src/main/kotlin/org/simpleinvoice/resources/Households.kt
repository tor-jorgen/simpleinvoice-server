package com.example.org.simpleinvoice.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import org.simpleinvoice.common.UUIDSerializer
import java.util.UUID

@Resource("/households")
class Households {
    @Resource("{id}")
    class Id(
        val parent: Households = Households(),
        @Serializable(with = UUIDSerializer::class) val id: UUID,
    )
}
