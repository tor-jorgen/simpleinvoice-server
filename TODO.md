# TODO

1. ~~Fail if wrong id on PUT /settings~~
2. Do not use Kotlin serialization, as it does not support all types, e.g., Instant and java.util.UUID. Currently, there
   is a mixture of Uuid and UUID, because Kotlin Serialization supports Uuid, while Exposed supports UUID...
3. Fix Postgres authentication. The current method is trust, which is not secure. Anyway, this should be OK for a single
   user setup on local machine
4. ~~Update repositories to return domain objects~~
5. ~~Return better error messages. E.g. when delete fails because of constraint violation~~
6. ~~Collect all database creation into one Flyway script before first release~~
7. Handle a list of emails in `SmtpClient.send`
8. Check if https://mvnrepository.com/artifact/com.itextpdf/itextpdf can be used to generate PDFs
9. ~~Use localized dates on the invoice document~~
10. ~~Possible to configure CORS og CSRF~~
11. ~~Add a build stage to Dockerfile~~
12. Check that an invoice is editable before updating it
13. How to handle rounding of monetary values? We only show two digits per invoice line, but we should not round off
    before the total
14. Store files in an emulated S3 bucket
15. 