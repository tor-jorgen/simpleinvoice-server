package org.simpleinvoice.repository

import com.example.org.simpleinvoice.repository.CustomerRepositoryInterface
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.simpleinvoice.model.Customer
import org.simpleinvoice.repository.model.CustomerDAO
import org.simpleinvoice.repository.model.CustomerTable
import java.util.UUID

class CustomerRepository : CustomerRepositoryInterface {
    override suspend fun all(): List<Customer> =
        suspendTransaction {
            CustomerDAO.all().map { it.toCustomer() }
        }

    override suspend fun add(customer: Customer): Unit =
        suspendTransaction {
            CustomerDAO.new {
                firstName = customer.firstName
                lastName = customer.lastName
                emailAddress = customer.emailAddress
                address = customer.address
                zipCode = customer.zipCode
                city = customer.city
                phoneNumber = customer.phoneNumber
            }
        }

    override suspend fun update(customer: Customer): Unit =
        suspendTransaction {
            CustomerDAO.findByIdAndUpdate(id = customer.id) {
                it.firstName = customer.firstName
                it.lastName = customer.lastName
                it.emailAddress = customer.emailAddress
                it.address = customer.address
                it.zipCode = customer.zipCode
                it.city = customer.city
                it.phoneNumber = customer.phoneNumber
            }
        }

    override suspend fun delete(id: UUID): Boolean =
        suspendTransaction {
            val rowsDeleted =
                CustomerTable.deleteWhere {
                    CustomerTable.id eq id
                }
            rowsDeleted == 1
        }
}
