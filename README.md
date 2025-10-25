# Simple Invoice Server

This is the server for Simple Invoice. It provides an HTTP API used by the Simple Invoice App. The server is built
using [Ktor](https://ktor.io).

In addition to the server, the backend consists of a Postgres database to store the data. The database is created
automatically when the server is run for the first time.

The Simple Invoice App is a web application that uses the server API to manage invoices. The Simple Invoice App can be
found at [Simple Invoice App](https://github.com/tor-jorgen/simpleinvoice-app).

Simple Invoice currently runs on a local computer and only supports a single user. However, both the app and the server
run in Docker containers, so it is possible to run them on a server (e.g. in the cloud). Anyway, that requires some more
work, and support for that will hopefully be added in the future.

**Note!** Simple Invoice should be run from this project, but the Simple Invoice App needs to be downloaded (cloned)
first.

The project started as [SimpleInvoice](https://github.com/tor-jorgen/simpleinvoice) - a simple command line tool.

## Required software

The following software is needed to build and run Simple Invoice backend and app.

### Linux

* Docker

### Mac

* Docker (using Homebrew)
* or, Docker Desktop

The shell scripts are made for Linux, but will hopefully work on a Mac as well.

### Windows

* WSL (Windows subsystem for Linux)
* Docker Desktop

**Note!** For Windows, you need to install Docker Desktop, which again requires Windows Subsystem for Linux (WSL) with
a Linux distribution. Depending on Windows version, you can use virtualization or Hyper-V instead, but WSL should work
for all versions.

If you have the required software installed, you can jump to the [Configuration](#Configuration) section.

## Windows Subsystem for Linux

The rest of the document assumes that the operating system is Linux, or that WSL is installed, so that Linux shell
scripts can be run.

### Installing WSL

Do the following to install WSL:

1. Open `Turn Windows features on or off`
2. Check `Windows Subsystem for Linux` and follow instructions
3. Open Powershell and run:
   ````shell
   wsl --install -d Ubuntu
   ````
   ``Ubuntu`` can be replaced by the preferred Linux distro.

To run commands/scripts from WSL, do the following:

1. Open PowerShell
2. Click on the down arrow in the menu bar and select the distro you installed to open a terminal

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

Follow instruction on https://docs.docker.com/desktop/setup/install/windows-install/ to Install Docker Desktop.

## Configuration

Before you run Simple Invoice, you need to configure it.

### Server, database, and app configuration

Many of the settings have default values that should work out of the box. You normally don't have to change those. The
settings without default values have to be set up. The following table shows all the possible properties that must/can
be configured:

| Property (`application.yaml`) | Environment variable        | Default value                            | Description                                                                                                                                                               |
|-------------------------------|-----------------------------|------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ktor.deployment.port`        | `SERVER_PORT`               | `8080`                                   | The port the server runs at                                                                                                                                               |     
| `db.connectionPrefix`         | `DB_CONNECTION_PRE`         | `jdbc:postgresql://host.docker.internal` | The prefix for the database connection string. This includes the string up to (but not including) the colon before the port                                               |     
| `db.port`                     | `DB_PORT`                   | `5432`                                   | The port the database server runs at                                                                                                                                      |     
| `db.name`                     | `DB_NAME`                   | `simple_invoice`                         | The name of the database                                                                                                                                                  |
| `db.user`                     | `DB_USER`                   | `db`                                     | The name of the user used to connect to the database                                                                                                                      |     
| `db.password`                 | `DB_PASSWORD`               |                                          | The password for the user used to connect to the database                                                                                                                 |
| `security.clientId`           | `GOOGLE_CLIENT_ID`          |                                          | OAuth 2 client ID (not yet in use, and it does not have to be set, but you will avoid a warning if you set it to any value)                                               |     
| `security.clientSecret`       | `GOOGLE_CLIENT_SECRET`      |                                          | OAuth 2 client secret  (not yet in use, and it does not have to be set, but you will avoid a warning if you set it to any value)                                          |
| `security.allowHosts`         | `ALLOW_HOSTS`               | `http://localhost:8000`                  | URL for hosts allowed to call the server. These are used for both CORS and CSRF configuration                                                                             |
| `security.csrfToken`          | `CSRF_TOKEN`                |                                          | The CSRF token token to use. This can be set to any value. You don't have to set this if you only run the backend                                                         |
| `smtp.host`                   | `SMTP_HOST`                 | `smtp.gmail.com`                         | The SMTP server host URL. Needed if it should be possible to send an email with the invoice                                                                               |
| `smtp.port`                   | `SMTP_PORT`                 | `587`                                    | The port the SMTP server runs at. Needed if it should be possible to send an email with the invoice                                                                       |                                                                                                                                                                                                                                                                          
| `smtp.tls`                    | `SMTP_TLS`                  | `true`                                   | `true` if communication with the SMTP server should use TLS (secure communication). Highly recommended. Needed if it should be possible to send an email with the invoice |                                                                                                                                                                                                                                                                          
| `smtp.usernName`              | `SMTP_USER_NAME`            |                                          | The user name to use when logging on to the SMTP server                                                                                                                   |
| `smtp.password`               | `SMTP_PASSWORD`             |                                          | The password to use when logging on to the SMTP server                                                                                                                    |
| `smtp.senderEmail`            | `SMTP_SENDER_EMAIL`         |                                          | The email address to use as the sender of the emails                                                                                                                      |
| `smtp.senderName`             | `SMTP_SENDER_NAME`          |                                          | The name to use as the sender of the emails                                                                                                                               |
| `invoice.invoiceDirectory`    | `INVOICE_INVOICE_DIRECTORY` | `/documents`                             | The directory in which to store invoices generated by Simple Invoice (note that this is the directory within the Docker image)                                            |
| `invoice.invoiceTemplate`     | `INVOICE_INVOICE_TEMPLATE`  | `./config/invoice.odt`                   | The path to the invoice document template (note that this points to a local directory for easy access)                                                                    |
| `invoice.invoiceName`         | `INVOICE_INVOICE_NAME`      | `_NO_-_HOUSEHOLD_`                       | The name of the generated invoice files. The default will give _<invoice number>-<household name>.<extension>. See below for more information                             |
|                               | `CFG_PATH`                  | `./config`                               | The path to the directory where the configuration files are stored (note that this points to a local directory for easy access)                                           |     
|                               | `API_BASE_URL`              | `http://localhost:8080`                  | The URL to the Simple Invoice server API                                                                                                                                  |
|                               | `APP_BUILD_CONTEXT`         | `../simpleinvoice-app`                   | The path to the simple invoice App, relative to Simple Invoice Server root directory                                                                                      |
|                               | `APP_BUILD_DOCKERFILE`      | `Dockerfile`                             | The name of the Dockerfile used to build the Simple Invoice App Docker image                                                                                              |
|                               | `APP_PORT`                  | `8000`                                   | The port that the app will be available at                                                                                                                                |

Note that the address to `localhost` is `host.docker.internal` inside Docker. This is a special DNS name that resolves
to the internal IP address of the host.

To configure the system, create a `.env` file in the project root directory, and add environment variables to it. This
file is used when running the system in Docker. Below is a typical `.env` file:

`````properties
DB_PASSWORD=ef87bd37-cec4-4e5d-93c9-1e5eb56acda3
SMTP_USER_NAME=harry.kure@gmail.com
SMTP_PASSWORD=77d38251-2184-4539-b44d-a1fe9d019063
SMTP_SENDER_EMAIL=harry.kure@gmail.com
SMTP_SENDER_NAME=Harry Kure
CSRF_TOKEN=4581e4c0-39d1-4fc3-9ce6-8feee2269ee2
GOOGLE_CLIENT_ID=XXX
GOOGLE_CLIENT_SECRET=YYY
`````

### Invoice template configuration

The invoice template is an Open Document Text (ODT) file used to generate the PDF invoice files. The template
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

### Invoice name configuration

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

## Running Simple Invoice

All the components will run in Docker.

Run Simple Invoice with the following command:

```shell
./start.sh
```

Add `--help` to see all the options.

The Simple Invoice App can be reached at http://localhost:8000.

Run the following command to stop Simple invoice:

````shell
./stop.sh
````

## Maintenance

The database and the invoice document storage will be created by the server the first time Simple Invoice is run, and
they will be placed in directories determined by Docker.

### Backing up data

It's a good idea to stop Simple Invoice before backing up the data.

Run the following command to back up the database, invoice documents, and configuration:

```shell
./backup.sh
```

Run command with `--help` to get help.

You can back up to the cloud, e.g., Dropbox, by setting up a desktop client or a daemon, and back up to the directory
used by the client/daemon.

**Note!** The configuration (`.env`) is not backup up, since it contains secrets. This file must be copied manually to a
safe place.

### Restoring data

This is a manual process, since we don't know which data to restore.

Run the following commands to find out where docker stores the database and the invoice documents:

```shell
docker volume inspect simple-invoice-db-data
docker volume inspect simple-invoice-documents
```

## Licence

[![License: GNU GPL v3.0](https://img.shields.io/badge/License-GNU%20GPL%20v3.0-brightgreen.svg)](https://choosealicense.com/licenses/gpl-3.0/)

## Development

See [Simple Invoice App Development](src/README.md)

## Contact

[dev@johannessenweb.com](mailto:dev@johannessenweb.com)
