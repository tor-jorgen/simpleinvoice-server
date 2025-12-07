# Simple Invoice Server Development

## Required software

The following software is needed to develop and debug the backend (in addition to the software needed to run the
server):

* Java 21
* IntelliJ IDEA (or any other IDE that supports Kotlin and Ktor)

## Installing Java

Java 21 is the latest LTS version supported by Kotlin.

### Linux

Run the following commands to install Java with SdkMan:

````shell
sudo apt install zip
sudo apt install unzip
curl -s "https://get.sdkman.io" | bash
# Use latest 21 version
sdk install java 21.0.9-amzn
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

Se [Configuration](../README.md#configuration) for information on how to configure the backend.

In addition to the typical configuration, you need to add the following environment variable to the `.env` file to allow
traffic from the Simple Invoice App running in React development mode:

```shell
ALLOW_HOSTS=http://localhost:5173
```

## Running/debugging server from IDE

The database will run in Docker, and the server will run in IntelliJ (or any other IDE). The description below is for
IntelliJ:

1. Start the Postgres database in Docker:
    ```shell
   docker compose -f compose-postgres.yaml up
    ```

2. Create either (under Services in IntelliJ):
    1. A Ktor run configuration for `EngineMain`
    2. or a Ktor debug configuration with Main class `org.simpleinvoice.server.ApplicationKt`

3. Run the following command to get the environment variables needed:
   ```shell
   ./build.sh --list-env

4. Copy all the environment variables and paste the environment variables in the run configuration:
    1. Select _Edit environment variables_
    2. Click the paste button

   You should also set `INVOICE_INVOICE_DIRECTORY` to e.g. `./.documents`, since the default values points to a
   directory within the Docker image.

5. Run or debug the configuration

## Running backend in Docker

This is helpful when you develop the Simple Invoice App.

Run the following command to run backend (server and database) in Docker:

```shell
docker compose -f compose-backend.yaml up
```

## Running backend and app in Docker

This will build both backend and frontend before running.

Run the following command to run the complete system in Docker:

```shell
docker compose -f compose-all.yaml up
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
docker volume rm simple-invoice-documents
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
./gradlew --write-verification-metadata pgp,sha256 --export-keys [--refresh-dependencies]
```

You should build the project after creating the metadata to verify that the metadata is correct.

Sometimes, metadata is missing, and artifacts must be added to the `<trusted-artifacts>` section in
`verification-metadata.xml`. Be careful when doing that.

Ideally checksums should be updated manually, but if you do it automatically, as above, be sure you validate the updated
`verification-metadata.xml` before you commit it.

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
