# TODO

1. Fail if wrong id on PUT /settings
2. Do not use Kotlin serialization, as it does not support all types, e.g. Instant and java.util.UUID. Currently, there
   is a mixture of Uuid and UUID, because Kotlin Serialization support Uuid, while Exposed supports UUID...
3. Fix Postgres authentication
4. Update repositories to return domain objects
5. Generating invoice in database failed, but invoice was created in DB, as well as a document
6. Return better error messages. E.g. when delete fails because of constraint violation
