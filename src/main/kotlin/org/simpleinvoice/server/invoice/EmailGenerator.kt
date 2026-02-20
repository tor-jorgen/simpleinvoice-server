package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.resources.model.EmailRequest
import org.simpleinvoice.server.util.smtp.SmtpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EmailGenerator(
    private val smtpClient: SmtpClient,
    private val eventPublisher: EventPublisher,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun sendEmail(
        invoice: Invoice,
        invoiceName: String,
        pdfPath: String,
        email: EmailRequest,
    ): Boolean {
        val recipients = RecipientList.fromHouseHold(invoice.household)
        val recipientEmails = recipients.mapNotNull { it.email }.filter { it.isNotBlank() }
        if (recipientEmails.isEmpty()) {
            logger.error("Cannot send e-mail to {} because e-mail addresses are missing.", invoice.household.name)
            eventPublisher.publishIdEvent(
                invoice.id,
                "Cannot send e-mail to ${invoice.household.description()}, because e-mail address is missing.",
            )
            return false
        }

        val invoicePath = pdfPath
        val invoiceFileName = "$invoiceName.pdf"
        val emailSent =
            smtpClient.openAndSend(
                subject = email.subject,
                text = email.text ?: "",
                toEmail1 = recipientEmails.first(),
                toEmail2 = if (recipientEmails.size > 1) recipientEmails[1] else null,
                invoicePath = invoicePath,
                invoiceName = invoiceFileName,
            )
        val emails = recipientEmails.joinToString(", ")
        if (emailSent) {
            eventPublisher.publishIdEvent(
                invoice.id,
                "Invoice ${invoice.invoiceNumber} sent to $emails",
            )
        } else {
            eventPublisher.publishIdEvent(
                invoice.id,
                "Failed to send invoice ${invoice.invoiceNumber} to $emails",
            )
        }
        return emailSent
    }
}
