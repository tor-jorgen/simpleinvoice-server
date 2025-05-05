package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Invoice
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import util.smtp.SmtpClient

class EmailGenerator(
    private val invoiceConfig: InvoiceBatchConfig,
    private val eventPublisher: EventPublisher,
    smtpClient: SmtpClient,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private val client: SmtpClient? = if (invoiceConfig.sendEmail()) smtpClient.open() else null

    suspend fun sendEmail(
        invoice: Invoice,
        invoiceName: String,
        odtPath: String,
        pdfPath: String,
    ): Boolean {
        if (!invoiceConfig.sendEmail()) {
            logger.warn("e-mail sending is disabled in the configuration.")
            eventPublisher.publishIdEvent(
                invoice.id,
                "Cannot send e-mail to ${invoice.household.description()}, because e-mail sending is disabled in the configuration.",
            )
            return false
        }

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

        val invoicePath = if (invoiceConfig.generatePdf()) pdfPath else odtPath
        val invoiceFileName = "$invoiceName.${if (invoiceConfig.generatePdf()) "pdf" else "odt"}"
        val emailSent =
            client!!.send(
                subject = invoiceConfig.emailSubject,
                text = invoiceConfig.emailText,
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
