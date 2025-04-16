package org.simpleinvoice.resources

import io.ktor.resources.Resource

@Resource("/persons")
class Persons {
    @Resource("{id}")
    class Id(
        val parent: Persons = Persons(),
        val id: Long,
    )
}
