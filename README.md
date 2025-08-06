# Simple Invoice Server

This is the server for the Simple Invoice project. It provides an HTTP API that is used by the Simple Invoice App.

The server is built using [Ktor](https://ktor.io).

## Prerequisites

The following software is needed to build and run Simple Invoice backend and app:

* Java 24
* WSL (Windows subsystem for Linux - for Windows)
* Docker or Docker Desktop (for Windows)
* A prebuilt Docker image of the Simple Invoice App (
  see [Simple Invoice App](https://github.com/tor-jorgen/simpleinvoice-app))

Java is only needed to build the Docker image.

**Note!** For Windows, you need to install Windows Desktop, which again requires Windows Subsystem for Linux (WSL) with
a Linux distribution. Depending on Windows version, you can use virtualization or Hyper-V instead, but WSL should work
for all versions.

## Configuration

Before you run the server, you need to configure it.

### Server configuration

Many of the settings have default values that should work out of the box. The settings without default values have to be
set up. The following table shows all the possible properties that must/can be configured:

| Property (`allication.yaml`) | Environment variable        | Default value                 | Description                                                                                                                                   |
|------------------------------|-----------------------------|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `ktor.deployment.port`       | `SERVER_PORT`               | `8080`                        | The port the server runs at                                                                                                                   |     
| `db.connectionPrefix`        | `DB_CONNECTION_PRE`         | `jdbc:postgresql://localhost` | The prefix for the database connection string. This includes the string up to (but not including) the colon before the port                   |     
| `db.port`                    | `DB_PORT`                   | `5432`                        | The port the database server runs at                                                                                                          |     
| `db.name`                    | `DB_NAME`                   | `simple_invoice`              | The name of the database                                                                                                                      |
| `db.user`                    | `DB_USER`                   | `db`                          | The name of the user used to connect to the database                                                                                          |     
| `db.password`                | `DB_PASSWORD`               |                               | The password for the user used to connect to the database                                                                                     |
| `security.clientId`          | `GOOGLE_CLIENT_ID`          |                               | OAuth 2 client ID (not yet in use)                                                                                                            |     
| `security.clientSecret`      | `GOOGLE_CLIENT_SECRET`      |                               | OAuth 2 client secret  (not yet in use)                                                                                                       |
| `smtp.host`                  | `$SMTP_HOST`                | `smtp.gmail.com`              |                                                                                                                                               |
| `smtp.port`                  | `SMTP_PORT`                 | `587`                         |                                                                                                                                               |                                                                                                                             |
| `smtp.tls`                   | `SMTP_TLS`                  | `true`                        |                                                                                                                                               |
| `smtp.usernName`             | `SMTP_USER_NAME`            |                               |                                                                                                                                               |
| `smtp.password`              | `SMTP_PASSWORD`             |                               |                                                                                                                                               |
| `smtp.senderEmail`           | `SMTP_SENDER_EMAIL`         |                               |                                                                                                                                               |
| `smtp.senderName`            | `SMTP_SENDER_NAME`          |                               |                                                                                                                                               |
| `invoice.invoiceDirectory`   | `INVOICE_INVOICE_DIRECTORY` | `./documents`                 |                                                                                                                                               |
| `invoice.invoiceTemplate`    | `INVOICE_INVOICE_TEMPLATE`  | `./config/invoice.odt`        |                                                                                                                                               |
| `invoice.invoiceName`        | `INVOICE_INVOICE_NAME`      | `_NO_-_HOUSEHOLD_`            | The name of the generated invoice files. The default will give _<invoice number>-<household name>.<extension>. See below for more information |
|                              | `CFG_PATH`                  | `./.config`                   | The path to the directory where the configuration files are stored                                                                            |     
|                              | `DOCUMENT_PATH`             | `./.documents`                | The path to the directory where the data files are stored                                                                                     |     
|                              | `DB_DATA_PATH`              | `./.postgres`                 | The path to the directory where the database files are stored. It will be created automatically when the database starts                      |
|                              | `REACT_APP_API_BASE_URL`    | `http://localhost:8080`       | The URL to the Simple Invoice server API. You don't have to set this if you only run the backend                                              |
|                              | `REACT_APP_CSRF_TOKEN`      |                               | The CSRF token token to use. This can be set to any value. You don't have to set this if you only run the backend                             |

Set the environment variables in a `.env` file in the project root directory. This file is used when running the server
backend from Docker. E.g.:

`````properties
DB_PASSWORD=ef87bd37-cec4-4e5d-93c9-1e5eb56acda3
GOOGLE_CLIENT_ID=XXX
GOOGLE_CLIENT_SECRET=YYY
SMTP_USER_NAME=harry.kure@gmail.com
SMTP_PASSWORD=77d38251-2184-4539-b44d-a1fe9d019063
SMTP_SENDER_EMAIL=harry.kure@gmail.com
SMTP_SENDER_NAME=Harry Kure
REACT_APP_CSRF_TOKEN=d2477104-adc0-405f-a2eb-7b49c1371ea3
`````

Note that the address to localhost is `host.docker.internal` inside Docker. This is a special DNS name that resolves to
the internal IP address of the host.

### Invoice template configuration

The invoice template is an Open Document Text (ODT) file that is used to generate the invoice PDF files. The template
can be customized to include the information you want in the invoice. The template file should be placed in the
directory specified by the `INVOICE_INVOICE_TEMPLATE` environment variable (see above for more information).
See [example-templates](./example-templates) for examples.

The following table shows the name of the placeholders that can be used in an invoice template:

| Placeholder name | Description                                                   |
|------------------|---------------------------------------------------------------|
| `_NO_`           | Invoice number                                                |
| `_DATE_`         | Generated date                                                |
| `_DUE_DATE_`     | Due data                                                      |
| `_HOUSEHOLD_`    | Name of household                                             |
| `_ADDRESS1_`     | Address 1                                                     |
| `_ADDRESS2_`     | Address 2                                                     |
| `_ZIP_CITY_`     | Zip code and city                                             |
| `_COUNTRY_`      | Country                                                       |
| `_NAME1_`        | First name and lastname of the first person in the household  |
| `_NAME2_`        | First name and lastname of the second person in the household |
| `_PRODUCT_`      | Name of the product in the invoice line                       |
| `_PRICE_`        | Price of the product in the invoice line                      |
| `_TOTAL_`        | Total price for the invoice                                   |

### Invoice name

The following list shows the placeholders that can be used to create the invoice name:

| Placeholder name | Description                                                  |
|------------------|--------------------------------------------------------------|
| `_NO_`           | Invoice number                                               |
| `_DATE_`         | Generated date                                               |
| `_DUE_DATE_`     | Due data                                                     |
| `_HOUSEHOLD_`    | Name of household                                            |
| `_ADDRESS1_`     | Address 1                                                    |
| `_NAME1_`        | First name and lastname of the first person in the household |
| `_PRODUCT1_`     | Name of the product in the first invoice line                |

All spaces will be removed when creating the invoice name. See `INVOICE_INVOICE_NAME` above for more information.

## Running locally

All the applications of the backend will run in Docker. This includes the database and the server.

To avoid problems with directory access, create the `DATA_PATH` directory manually before you start.

### Installing Java

````shell
sudo apt install zip
sudo apt install unzip
curl -s "https://get.sdkman.io" | bash
sdk install java 24.0.2-amzn
````

### Installing Docker

### Ubuntu

Run the following commands to install Docker on Ubuntu:

```shell
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker <your user name>
newgrp docker
```

This will add the user `<your user name>` to the group `docker`, so that you can run Docker as that user.

### Windows

Install Docker Desktop...

### Building the backend

The first time you run the backend from Docker, you need to build it. Do this by running the following command:

TODO: Add build step to Dockerfile?

```shell
./build.sh
```  

Add `--help to see all options.`

### Running the backend and the frontend

You need to build the frontend Docker image first.
See [Simple Invoice App](https://github.com/tor-jorgen/simpleinvoice-app) for more information.

Run the following command to start both backend and frontend in Docker:

```shell
./start.sh
```

Add `--help to see all options.`

Run the following command to stop the applications:

````shell
./stop.sh
````

### Running the backend

To run both the server and the database in Docker, use the following command:

## Debugging locally

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
```

4. Copy all the environment variables and paste the environment variables in the run configuration:
    1. Select _Edit environment variables_
    2. Click the past button
4. Run or debug the configuration

## Database maintenance

The database will be created by the server the first time it is run, and it will be stored under the path specified by
the `DATA_PATH` environment variable.

### Deleting the database

Run the following to delete the database:

```shell
sudo rm -rf <DATA_PATH environment variable>
``` 

## Backup data

Run the following commands:

```shell
sudo zip -r simpleinvoice <data directory>/
chown <user>:<group> simpleinvoice.zip
```
