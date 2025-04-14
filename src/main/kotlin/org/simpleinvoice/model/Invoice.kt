package com.example.org.simpleinvoice.model

import kotlinx.serialization.Serializable

@Serializable
class Invoice(
    val number: String,
    val generated: String,
    val dueDate: String,
    val customer: Customer,
    val invoicelines: List<InvoiceLine>,
)
