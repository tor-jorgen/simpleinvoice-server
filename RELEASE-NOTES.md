## 1.0.1 (2026-02-18)

* Possible to store default tax percentage and default currency
* Calculate on server instead of using calculations from frontend
* Possible to run locally with images from GitHub or built locally
* Access Flyway files by filesystem instead of by classpath, as by classpath did not work in a fatjar for Flyway versions > 11.12.0
* Updated database with length of VARCHARs
* Created separate response classes for API
* Use a common response format
* Removed CSRF protection as it is not in use now and will probably not be needed in the future (as we probably wil go for token based authentication)
* Added scripts for the HTTP Client
* Upgraded libraries and frameworks
* Added more tests

## 1.0.0 (2025-12-01)

* First version
