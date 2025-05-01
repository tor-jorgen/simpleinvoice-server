package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.model.Invoice
import util.smtp.SmtpClient

class EmailGenerator(
    config: InvoiceConfig,
    private val invoiceConfig: InvoiceBatchConfig,
) {
    private val smtpClient: SmtpClient? = if (invoiceConfig.sendEmail()) SmtpClient(config.smtp).open() else null

    fun sendEmail(
        invoice: Invoice,
        invoiceName: String,
        odtPath: String,
        pdfPath: String,
    ): Boolean {
        if (!invoiceConfig.sendEmail()) {
            println("E-mail sending is disabled in the configuration.")
            return false
        }

        val recipients = RecipientList.fromHouseHold(invoice.household)
        val recipient1 = recipients[0].email
        if (recipient1.isBlank()) {
            println("Cannot send e-mail to ${recipients[0].name}, because e-mail address is missing.")
            return false
        }

        val invoicePath = if (invoiceConfig.generatePdf()) pdfPath else odtPath
        val invoiceFileName = "$invoiceName.${if (invoiceConfig.generatePdf()) "pdf" else "odt"}"
        val recipient2 = if (recipients.size > 1) recipients[1].email else null
        return smtpClient!!.send(
            invoiceConfig.emailSubject,
            invoiceConfig.emailText,
            recipient1,
            recipient2,
            invoicePath,
            invoiceFileName,
        )
    }
}
