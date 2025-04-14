package org.simpleinvoice.resources

import io.ktor.resources.Resource

@Resource("/customers")
class Customers {
//    @Resource("new")
//    class New(
//        val parent: Customers = Customers(),
//    )

    @Resource("{id}")
    class Id(
        val parent: Customers = Customers(),
        val id: Long,
    ) {
//        @Resource("edit")
//        class Edit(
//            val parent: Id,
//        )
    }
}
