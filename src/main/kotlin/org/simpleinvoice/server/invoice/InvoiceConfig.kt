package org.simpleinvoice.server.invoice

import org.simpleinvoice.server.util.yaml.YamlUtil
import util.smtp.SmtpConfig

data class InvoiceConfig(
    val invoiceDirectory: String,
    val smtp: SmtpConfig,
) {
    companion object {
        fun fromYaml(path: String): InvoiceConfig = YamlUtil.fromYaml(path)
    }
}
