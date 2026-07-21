# Simple Invoice Server Development

## Required software

The following software is needed to develop and debug the backend (in addition to the software needed to run the
server):

* Java 25
* IntelliJ IDEA (or any other IDE that supports Kotlin and Ktor)

## Installing Java

Java 25 is the latest LTS version supported by Kotlin.

### Linux

Run the following commands to install Java with SdkMan:

````shell
sudo apt install zip
sudo apt install unzip
curl -s "https://get.sdkman.io" | bash
# Use latest 25 version, e.g.:
sdk install java 25.0.2-amzn
````

SdkMan makes it easy to maintain more than one version of Java.

### Windows

If you need to install Java in Windows, you can use Scoop from PowerShell:

```shell
Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression
scoop bucket add java
scoop install java/corretto21-jdk
```

## Configuration

Se [Configuration](../README.md#configure-simple-invoice) for general information on how to configure the backend.

In addition, you need to add the following environment variable to the `.env` file to allow traffic from the Simple
Invoice App running in React development mode:

```properties
ALLOW_HOSTS=http://localhost:5173
```

### Optional server configuration

| Environment variable          | Default value                 | Description                                                                                                                                                               | Property (`application.yaml`) |
|-------------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------|
| `SERVER_PORT`                 | `8080`                        | The port the server runs at                                                                                                                                               | `ktor.deployment.port`        |     
| `DB_CONNECTION_PRE`           | `jdbc:postgresql://localhost` | The prefix for the database connection string. This includes the string up to (but not including) the colon before the port                                               | `db.connectionPrefix`         |     
| `DB_PORT`                     | `5432`                        | The port the database server runs at                                                                                                                                      | `db.port`                     |     
| `DB_NAME`                     | `simple_invoice`              | The name of the database                                                                                                                                                  | `db.name`                     |
| `DB_USER`                     | `db`                          | The name of the user used to connect to the database                                                                                                                      | `db.user`                     |     
| `S3_CONNECTION_PRE`           | `http://localhost`            | The prefix for the S3 connection string. This includes the string up to (but not including) the colon before the port                                                     | `s3.connectionPrefix`         |     
| `S3_PORT`                     | `9000`                        | The port the S3 server runs at                                                                                                                                            | `s3.port`                     |     
| `S3_ADM_PORT`                 | `9001`                        | The port the S3 admin UI runs at                                                                                                                                          |                               |
| `S3_ACCESS_KEY_ID`            | `doc`                         | The access key ID (user ID) for the S3 storage                                                                                                                            | `s3.accessKeyId`              |
| `$INVOICE_CONFIG_BUCKET_NAME` | `simple-invoice-config`       | The name of the S3 bucket that stores configuration files                                                                                                                 | `invoice.configBucketName`    |
| `INVOICE_BUCKET_NAME`         | `simple-invoice-invoices`     | The name of the S3 bucket that stores invoice files                                                                                                                       | `invoice.invoiceBucketName`   |
| `ALLOW_HOSTS`                 | `http://localhost:8000`       | URL for hosts allowed to call the server. These are used for CORS configuration                                                                                           | `security.allowHosts`         |
| `SMTP_HOST`                   | `smtp.gmail.com`              | The SMTP server host URL. Needed if it should be possible to send an email with the invoice                                                                               | `smtp.host`                   |
| `SMTP_PORT`                   | `587`                         | The port the SMTP server runs at. Needed if it should be possible to send an email with the invoice                                                                       | `smtp.port`                   |                                                                                                                                                                                                                                                                          
| `SMTP_TLS`                    | `true`                        | `true` if communication with the SMTP server should use TLS (secure communication). Highly recommended. Needed if it should be possible to send an email with the invoice | `smtp.tls`                    |

### Optional app configuration

The app can also be configured. This is because the app is started together with the server.

| Environment variable   | Default value           | Description                                                                          |
|------------------------|-------------------------|--------------------------------------------------------------------------------------|
| `API_BASE_URL`         | `http://localhost:8080` | The URL to the Simple Invoice server API                                             |
| `APP_BUILD_CONTEXT`    | `../simpleinvoice-app`  | The path to the simple invoice App, relative to Simple Invoice Server root directory |
| `APP_BUILD_DOCKERFILE` | `Dockerfile`            | The name of the Dockerfile used to build the Simple Invoice App Docker image         |

**Note!** Changing these properties requires that you rebuild the app Docker image, since the properties are replaced in
the web application.

## Running/debugging server from IDE

The database will run in Docker, and the server will run in IntelliJ (or any other IDE). The description below is for
IntelliJ:

1. Start the Postgres database in Docker:
    ```shell
   docker compose -f compose-postgres-s3.yaml up
    ```

2. Create the following run configuration (under Services in IntelliJ):
    1. Ktor with Main class `org.simpleinvoice.server.ApplicationKt`
    2. Set the following environment variables:
       ```
       ALLOW_HOSTS=http://localhost:5173
       ``` 
       The content of the `.env` file will also be read when the server starts
3. Run or debug the configuration

## Running backend in Docker

This is helpful when you develop the Simple Invoice App.

Run the following commands to run backend (server and database) in Docker:

```shell
docker compose -f compose-backend.yaml build [--no-cache]
docker compose -f compose-backend.yaml up
```

## Running backend and app in Docker

This will build both backend and frontend before running.

Run the following commands to run the complete system in Docker:

```shell
docker compose build [--no-cache]
docker compose up
```

You can also run the following script:

```shell
./start.sh -h
```

## Database maintenance

The database will be created by the server the first time it is run.

The database is placed in a directory determined by Docker.

## Backing up data

Run the following command to back up the database, invoice documents, and configuration:

```shell
./backup.sh
```

Run command with `--help` to get help.

## Deleting the database

Run the following to delete the database:

```shell
docker volume rm simple-invoice-db-data
``` 

## Deleting the invoice documents

Run the following to delete the documents:

```shell
docker volume rm simple-invoice-s3-data
``` 

## Dependency verification

Dependency verification is enabled. The following are verified:

* Artifact checksums
* Artifact signatures (if available)
* Metadata checksums
* Metadata signatures (if available)

Build is set up to use local keyring only (`key-servers enabled="false"`). If dependency validation fails, you have to
update the verification metadata and/or verification keyring. Run the following command to update the verification
metadata and verification keyring:

```shell
./gradlew --write-verification-metadata pgp,sha256 --export-keys --refresh-dependencies
```

You should build the project after creating the metadata to verify that the metadata is correct.

Sometimes, metadata is missing, and artifacts must be added to the `<trusted-artifacts>` section in
`verification-metadata.xml`. Be careful when doing that.

Ideally checksums should be updated manually, but if you do it automatically, as above, be sure you validate the updated
`verification-metadata.xml` before you commit it.

If you get a problem with the verification, you can try to delete `gradle/verification-*.*`, and generate again, but you
should then add the following lines to `verification-metadata.xml` below `<verify-signatures>`:

```xml

<verification-metadata>
    <!-- ... -->
    <verify-signatures>true</verify-signatures>
    <keyring-format>armored</keyring-format>
    <key-servers enabled="false"/>
    <trusted-artifacts>
        <trust file=".*-sources[.]jar" regex="true"/>
        <trust file="gradle-[0-9.]+-src.zip" regex="true"/>
    </trusted-artifacts>
    <!-- ... -->
</verification-metadata>
```

| Setting                                    | Description                                                                                                     |
|--------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `<keyring-format>armored</keyring-format>` | Write keys to plain text (`.keys`) - ASCII-armored format                                                       |
| `<key-servers enabled="false"/>`           | Only use the local key file (`.keys`)                                                                           |
| `<trusted-artifacts>`                      | Artifacts to trust. In this case Gradle itself and downloded sources. IntelliJ will fail unless these are added |

## Lint Github workflows

Run the following scrip to lint the Github workflows:

```shell
./actionlint.sh
```

## Check image vulnerabilities

Run the following script to check the image for vulnerabilities:

```shell
./trivy.sh
```

Note that it will download the complete vulnerability database on the first run
