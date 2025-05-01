package org.simpleinvoice.server.invoice

import util.smtp.SmtpClient

class EmailGenerator(
    config: InvoiceConfig,
    private val invoiceConfig: InvoiceBatchConfig,
) {
    private val smtpClient: SmtpClient? = if (invoiceConfig.sendEmail()) SmtpClient(config.smtp).open() else null

    private fun sendEmail(
        recipients: List<Recipient>,
        invoiceName: String,
        odtPath: String,
        pdfPath: String,
    ) {
        if (!invoiceConfig.sendEmail()) {
            return
        }

        val recipient1 = recipients[0].email
        if (recipient1.isBlank()) {
            println("Cannot send e-mail to ${recipients[0].name}, because e-mail address is missing.")
            return
        }

        val invoicePath = if (invoiceConfig.generatePdf()) pdfPath else odtPath
        val invoiceFileName = "$invoiceName.${if (invoiceConfig.generatePdf()) "pdf" else "odt"}"
        val recipient2 = if (recipients.size > 1) recipients[1].email else null
        smtpClient!!.send(
            invoiceConfig.emailSubject,
            invoiceConfig.emailText,
            recipient1,
            recipient2,
            invoicePath,
            invoiceFileName,
        )
    }
}
