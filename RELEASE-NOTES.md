# 2.0.0 (2026-08-30)

* Possible to add a message when creating/updating/deleting all kinds of data. Message will be stored in the audit
  trail. This breaks the API

# 1.3.0 (2026-08-23)

* Added bulk update for invoices. Only update of status is supported
* Possible to add a user message to the audit trail
* `-c` now works in `start.sh`
* `start.sh` shows the correct port for the app

# 1.2.3 (2026-07-21)

* Setting the `APP_PORT` environment variable to run the app on a different port now works

## 1.2.2 (2026-07-20)

* No need to set `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`
* Updated README files

## 1.2.1 (2026-07-08)

* Renamed and updated placeholders (`_LINE_TOTAL_PRICE_` and `_INVOICE_TOTAL_PRICE_`) to support total prices
* Fixed loading of e-mail attachments from S3 bucket
* Fixed `s3.sh` so that it handles relative paths
* Possible to delete all documents before restoring with `restore.sh`
* Upgraded to latest Gradle, and libraries

## 1.2.0 (2026-06-02)

* Documents are stored in S3-compatible storage
* Upgraded to latest Kotlin, Gradle, and libraries
* Updated start/stop and backup/restore scripts
* Added util for handling S3-file (`s3.sh`)

## 1.1.0 (2026-05-14)

* Calculate tax and totals on the server
* Added restore functionality (`restore.sh`)
* Updated backup (use tar instead of zip)
* Added graceful shutdown of containers
* Fixed database migration script
* Upgraded to Java 25, and latest Kotlin, Gradle, and libraries
* Updated configuration parameters

## 1.0.2 (2026-02-22)

* Removed rest of CSRF

## 1.0.1 (2026-02-18)

* Possible to store default tax percentage and default currency
* Calculate on server instead of using calculations from frontend
* Possible to run locally with images from GitHub or built locally
* Access Flyway files by filesystem instead of by classpath, as by classpath did not work in a fatjar for Flyway
  versions > 11.12.0
* Updated database with length of VARCHARs
* Created separate response classes for API
* Use a common response format
* Removed CSRF protection as it is not in use now and will probably not be needed in the future (as we probably wil go
  for token based authentication)
* Added scripts for the HTTP Client
* Upgraded libraries and frameworks
* Added more tests

## 1.0.0 (2025-12-01)

* First version
