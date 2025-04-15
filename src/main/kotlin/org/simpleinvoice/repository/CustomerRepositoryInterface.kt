package com.example.org.simpleinvoice.repository

import org.simpleinvoice.model.Customer
import java.util.UUID

interface CustomerRepositoryInterface {
    suspend fun all(): List<Customer>

    suspend fun add(customer: Customer): Unit

    suspend fun update(customer: Customer): Unit

    suspend fun delete(id: UUID): Boolean
}
