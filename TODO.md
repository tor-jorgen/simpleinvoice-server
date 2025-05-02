# TODO

1. ~~Add sorting to columns~~
2. ~~Remove Register Persons~~
3. Fail if wrong id on PUT /settings
4. Possible to show a localized text for constants in dropdown lists
5. ~~Show a list of unpaid invoices on front page?~~
6. Add users
7. ~~Handle offline server the same way in all places~~
8. ~~Possible to search in dropdown lists for household and products in invoices~~
9. ~~Jump to "First name" when "ADD PERSON" is clicked in Register Households~~
10. Should not be possible to delete invoices
11. Do not use Kotlin serialization, as it does not support all types, e.g. Instant and java.util.UUID. Currently, there
    is a mixture of Uuid and UUID, because Kotlin Serialization support Uuid, while Exposed supports UUID...
12. Fix Postgres authentication