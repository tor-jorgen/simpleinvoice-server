package org.simpleinvoice.server.invoice

import org.odftoolkit.simple.TextDocument
import org.odt2pdf.PDFConverter
import org.simpleinvoice.server.model.Invoice
import org.simpleinvoice.server.util.s3.StorageClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

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
private const val PRODUCT_1 = "_PRODUCT1_"
private const val LINE_TOTAL_PRICE = "_LI_T_P_"
private const val INVOICE_TOTAL_PRICE = "_IN_T_P_"

class DocumentGenerator(
    private val config: InvoiceConfig,
    private val pdfConverter: PDFConverter,
    private val storageClient: StorageClient,
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun createDocuments(invoice: Invoice): Pair<String, String> {
        val bytes = storageClient.download(bucketName = config.configBucketName, keyName = config.invoiceTemplateName)
        TextDocument.loadDocument(ByteArrayInputStream(bytes)).use { document ->
            val recipients = RecipientList.fromHouseHold(invoice.household)
            traverse(node = document.contentRoot, invoice = invoice, recipients = recipients)
            traverse(node = document.header.odfElement, invoice = invoice, recipients = recipients)
            traverse(node = document.footer.odfElement, invoice = invoice, recipients = recipients)
            val invoiceName = getInvoiceName(invoice = invoice, recipients = recipients)
            val pdfPath = "$invoiceName.pdf"
            generatePdf(document, pdfPath)
            logger.info("Invoice {} generated for {}", pdfPath, recipients[0].addressLine1)
            return Pair(invoiceName, pdfPath)
        }
    }

    private fun getInvoiceName(
        invoice: Invoice,
        recipients: List<Recipient>,
    ): String {
        var invoiceName = config.invoiceName
        invoiceName = invoiceName.replace(INVOICE_NO, invoice.invoiceNumber.toString())
        invoiceName = invoiceName.replace(INVOICE_DATE, invoice.generatedDate.toString())
        invoiceName = invoiceName.replace(DUE_DATE, invoice.dueDate.toString())
        invoiceName = invoiceName.replace(HOUSEHOLD_NAME, invoice.household.name ?: "")
        invoiceName =
            invoiceName.replace(ADDRESS_LINE_1, if (recipients.isNotEmpty()) recipients[0].addressLine1 else "")
        invoiceName = invoiceName.replace(NAME_1, if (recipients.isNotEmpty()) recipients[0].name else "")
        invoiceName =
            invoiceName.replace(
                PRODUCT_1,
                invoice.invoiceLines
                    .first()
                    .product.name,
            )

        return invoiceName.replace(" ", "")
    }

    private suspend fun generatePdf(
        document: TextDocument,
        outPath: String,
    ) {
        val odfStream = ByteArrayOutputStream()
        document.save(odfStream)
        val pdfStream = pdfConverter.fromOdf(odfStream.toByteArray())
        storageClient.upload(
            bucketName = config.invoiceBucketName,
            keyName = outPath,
            fileBytes = pdfStream.toByteArray(),
        )
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
            INVOICE_DATE -> {
                node.textContent = invoice.generatedDateAsString()
            }

            DUE_DATE -> {
                node.textContent = invoice.dueDateAsString()
            }

            INVOICE_NO -> {
                node.textContent = invoice.invoiceNumber.toString()
            }

            NAME_1 -> {
                node.textContent = if (recipients.isNotEmpty()) recipients[0].name else ""
            }

            NAME_2 -> {
                node.textContent = if (recipients.size > 1) recipients[1].name else ""
            }

            ADDRESS_LINE_1 -> {
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].addressLine1 else ""
            }

            ADDRESS_LINE_2 -> {
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].addressLine2 else ""
            }

            ZIP_CITY -> {
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].zipCity else ""
            }

            COUNTRY -> {
                node.textContent =
                    if (recipients.isNotEmpty()) recipients[0].country else ""
            }

            // TODO: Add support for multiple items
            PRODUCT -> {
                node.textContent =
                    invoice.invoiceLines
                        .first()
                        .product.name
            }

            LINE_TOTAL_PRICE, INVOICE_TOTAL_PRICE -> {
                // TODO: This must be fixed if support for multiple lines is added
                node.textContent =
                    invoice.invoiceLines
                        .first()
                        .totalPrice
                        .toString()
            }
        }
    }
}
