package org.simpleinvoice.server.invoice

import org.odftoolkit.simple.TextDocument
import org.odt2pdf.PDFConverter
import org.simpleinvoice.server.model.Invoice
import org.w3c.dom.Node
import java.io.ByteArrayOutputStream
import java.io.File

private const val INVOICE_NO = "_NO_"
private const val INVOICE_DATE = "_DATE_"
private const val DUE_DATE = "_DUE_DATE_"
private const val HOUSEHOLD_NAME = "_HOUSEHOLD_"
private const val ADDRESS_LINE_1 = "_ADDRESS1_"
private const val ADDRESS_LINE_2 = "_ADDRESS2_"
private const val ZIP_CITY = "_ZIP_CITY_"
private const val COUNTRY = "_COUNTRY_"
private const val NAME_1 = "_NAME1_"
private const val NAME_2 = "_NAME2_"
private const val PRODUCT = "_PRODUCT_"
private const val ITEM_PRICE = "_PRICE_"
private const val TOTAL_PRICE = "_TOTAL_"

class DocumentGenerator(
    private val config: InvoiceConfig,
    private val invoiceConfig: InvoiceBatchConfig,
) {
    fun createDocuments(invoice: Invoice): Triple<String, String, String> {
        File(config.invoiceDirectory).mkdirs()
        val pdfConverter = initPdfConverter()
        TextDocument.loadDocument(File(invoiceConfig.template)).use { document ->
            val recipients = RecipientList.fromHouseHold(invoice.household)
            traverse(node = document.contentRoot, invoice = invoice, recipients = recipients)
            traverse(node = document.header.odfElement, invoice = invoice, recipients = recipients)
            traverse(node = document.footer.odfElement, invoice = invoice, recipients = recipients)
            val invoiceName = getInvoiceName(invoice = invoice, recipients = recipients)
            val odtPath = "${config.invoiceDirectory}/$invoiceName.odt"
            val pdfPath = odtPath.replace("odt", "pdf")
            generateOdf(document, odtPath)
            generatePdf(document, pdfPath, pdfConverter)
            println("Invoice $pdfPath generated for ${recipients[0].addressLine1}")
            return Triple(invoiceName, odtPath, pdfPath)
        }
    }

    private fun getInvoiceName(
        invoice: Invoice,
        recipients: List<Recipient>,
    ): String {
        var invoiceName = invoiceConfig.invoiceName
        invoiceName = invoiceName.replace(INVOICE_NO, invoice.invoiceNumber.toString())
        invoiceName = invoiceName.replace(INVOICE_DATE, invoice.generatedDate.toString())
        invoiceName = invoiceName.replace(DUE_DATE, invoice.dueDate.toString())
        invoiceName = invoiceName.replace(HOUSEHOLD_NAME, invoice.household.name ?: "")
        invoiceName =
            invoiceName.replace(ADDRESS_LINE_1, if (recipients.isNotEmpty()) recipients[0].addressLine1 else "")
        invoiceName = invoiceName.replace(NAME_1, if (recipients.isNotEmpty()) recipients[0].name else "")
        invoiceName =
            invoiceName.replace(
                PRODUCT,
                invoice.invoiceLines
                    .first()
                    .product.name,
            )

        return invoiceName
    }

    private fun initPdfConverter(): PDFConverter? = if (invoiceConfig.generatePdf()) PDFConverter() else null

    private fun generatePdf(
        document: TextDocument,
        outPath: String,
        pdfConverter: PDFConverter?,
    ) {
        if (invoiceConfig.generatePdf()) {
            val out = ByteArrayOutputStream()
            document.save(out)
            pdfConverter!!.fromOdf(out.toByteArray(), outPath)
        }
    }

    private fun generateOdf(
        document: TextDocument,
        invoicePath: String,
    ) {
        if (invoiceConfig.generateOdt()) {
            document.save(File(invoicePath))
        }
    }

    private fun traverse(
        node: Node,
        invoice: Invoice,
        recipients: List<Recipient>,
    ) {
        if (node.textContent.isNotBlank()) {
            replace(node = node, invoice = invoice, recipients = recipients)
        } else {
            for (i in 0 until node.childNodes.length) {
                traverse(node = node.childNodes.item(i), invoice, recipients = recipients)
            }
        }
    }

    private fun replace(
        node: Node,
        invoice: Invoice,
        recipients: List<Recipient>,
    ) {
        when (node.textContent) {
            INVOICE_DATE -> node.textContent = invoice.generatedDateAsString()
            DUE_DATE -> node.textContent = invoice.dueDateAsString()
            INVOICE_NO -> node.textContent = invoice.invoiceNumber.toString()
            NAME_1 -> node.textContent = if (recipients.isNotEmpty()) recipients[0].name else ""
            NAME_2 -> node.textContent = if (recipients.size > 1) recipients[1].name else ""
            ADDRESS_LINE_1 ->
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].addressLine1 else ""

            ADDRESS_LINE_2 ->
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].addressLine2 else ""

            ZIP_CITY ->
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].zipCity else ""

            COUNTRY ->
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].country else ""

            // TODO: Add support for multiple items
            PRODUCT ->
                node.textContent =
                    invoice.invoiceLines
                        .first()
                        .product.name

            ITEM_PRICE, TOTAL_PRICE ->
                node.textContent =
                    invoice.invoiceLines
                        .first()
                        .product.price
                        .toString()
        }
    }
}
