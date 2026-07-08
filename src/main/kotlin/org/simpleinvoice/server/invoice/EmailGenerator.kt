package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Email
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.util.s3.S3StorageClient
import org.simpleinvoice.server.util.smtp.SmtpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EmailGenerator(
    private val config: InvoiceConfig,
    private val storageClient: S3StorageClient,
    private val smtpClient: SmtpClient,
    private val eventPublisher: EventPublisher,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun sendEmail(
        invoice: Invoice,
        pdfPath: String,
        email: Email,
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

        val bytes = storageClient.download(bucketName = config.invoiceBucketName, keyName = pdfPath)
        val emailSent =
            smtpClient.openAndSend(
                subject = email.subject,
                text = email.text ?: "",
                toEmail1 = recipientEmails.first(),
                toEmail2 = if (recipientEmails.size > 1) recipientEmails[1] else null,
                invoicePath = pdfPath,
                invoiceBytes = bytes,
            )
        val emails = recipientEmails.joinToString(", ")
        if (emailSent) {
            eventPublisher.publishEvent(
                id = invoice.id,
                item = invoice,
                message = "Invoice ${invoice.invoiceNumber} sent to $emails",
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
