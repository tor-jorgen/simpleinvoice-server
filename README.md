# Simple Invoice Server

This is the server for Simple Invoice. It provides an HTTP API used by the Simple Invoice App. The server is built
using [Ktor](https://ktor.io).

In addition to the API, the backend consists of a Postgres database to store the data, and a S3-compatible storage to
store the invoice and template files. The database is created automatically when the server is run for the first time.

The Simple Invoice App is a web application that uses the API to manage invoices. The app can be found
at [Simple Invoice App](https://github.com/tor-jorgen/simpleinvoice-app) - however, the app will be downloaded
automatically when running Simple Invoice.

Simple Invoice currently runs on a local computer and only supports a single user. However, both the app and the server
run in Docker containers, so it is possible to run them on a server (e.g. in the cloud). Anyway, that requires some more
work, and support for that will hopefully be added in the future.

The project started as [SimpleInvoice](https://github.com/tor-jorgen/simpleinvoice) - a simple command line tool.

<figure>
  <img src="images/simple-invoice.png" width="800" alt="Simple Invoice">
  <figcaption>Screenshot from the Generate Invoices page in the Simple Invoice App</figcaption>
</figure>

The following languages are currently supported:

* English
* Norwegian bokmål

## Run Simple Invoice

Simple Invoice should be run from this repository (not
from [Simple Invoice App](https://github.com/tor-jorgen/simpleinvoice-app)).

To run it you need to:

1. Download this repository from GitHub to you local machine
2. [Install required software](#install-required-software)
3. [Configure Simple Invoice](#configure-simple-invoice)
4. [Start and stop Simple Invoice](#start-and-stop-simple-invoice)

## Install required software

The following software is needed to build and run Simple Invoice backend and app.

### Linux

* Docker

### Mac

* Docker (using Homebrew)
* or, Docker Desktop

The shell scripts are made for Linux (including WSL), but will hopefully work on a Mac as well, possibly with some
adjustments.

### Windows

* WSL (Windows subsystem for Linux)
* Docker Desktop

**Note!** For Windows, you need to install Docker Desktop, which again requires Windows Subsystem for Linux (WSL) with a
Linux distribution. Depending on Windows version, you can use virtualization or Hyper-V instead, but WSL should work for
all versions.

If you have the required software installed, you can jump to the [Configure Simple Invoice](#configure-simple-invoice)
section.

## Windows Subsystem for Linux

The rest of the document assumes that the operating system is Linux, or that WSL is installed, so that Linux shell
scripts can be run.

### Install WSL

Do the following to install WSL:

1. Open `Turn Windows features on or off`
2. Check `Windows Subsystem for Linux` and follow instructions
3. Open Powershell and run:
   ````shell
   wsl --install -d Ubuntu
   ````
   ``Ubuntu`` can be replaced by the preferred Linux distro.

### Command shell

To run commands/scripts from WSL, do the following:

1. Open PowerShell
2. Click on the down arrow in the menu bar and select the distro you installed to open a terminal

Your drives will be mounted at `/mnt/<drive>`, so to go to the Windows C drive run the following command:

````shell
cd /mnt/c
````

## Install Docker

[Docker](https://www.docker.com/) is used to run the backend (Simple Invoice Server and database) and the web server
that hosts the Simple Invoice App.

### Ubuntu

Go to the terminal and run the following commands to install Docker on Ubuntu:

```shell
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker <your user name>
newgrp docker
```

This will add the user `<your user name>` to the group `docker`, so that you can run Docker as that user.

### Windows

Follow instruction on https://docs.docker.com/desktop/setup/install/windows-install/ to Install Docker Desktop.

## Configure Simple Invoice

Before you run Simple Invoice the first time, you need to do some initial configuration. Other configuration can be done
from the app.

Create an empty file with the name `.env` in the project root directory, and add environment variables to it.

Below is an example of a `.env` file:

`````properties
DB_PASSWORD=ef87bd37-cec4-4e5d-93c9-1e5eb56acda3
S3_SECRET_ACCESS_KEY=4c35c70b-c8d7-40df-89e9-06b069ce85f6
SMTP_USER_NAME=harry.kure@gmail.com
SMTP_PASSWORD=77d38251-2184-4539-b44d-a1fe9d019063
SMTP_SENDER_EMAIL=harry.kure@gmail.com
SMTP_SENDER_NAME=Harry Kure
`````

**Note!** The `Property` column in the tables below are only used when developing Simple Invoice.

### Mandatory server configuration

| Environment variable   | Description                                                                | Property (`application.yaml`) |
|------------------------|----------------------------------------------------------------------------|-------------------------------|
| `DB_PASSWORD`          | The password (of your own choice) to use when connecting to the database   | `db.password`                 |
| `S3_SECRET_ACCESS_KEY` | The password (of your own choice) to use when connecting to the S3 storage | `s3.secretAccessKey`          |     
| `SMTP_USER_NAME`       | The user name to use when logging on to the SMTP server                    | `smtp.usernName`              |
| `SMTP_PASSWORD`        | The password to use when logging on to the SMTP server                     | `smtp.password`               |
| `SMTP_SENDER_EMAIL`    | The email address to use as the sender of the emails                       | `smtp.senderEmail`            |
| `SMTP_SENDER_NAME`     | The name to use as the sender of the emails                                | `smtp.senderName`             |

### Optional server configuration

| Environment variable    | Default value      | Description                                                                                                                                                 | Property (`application.yaml`) |
|-------------------------|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------|
| `INVOICE_TEMPLATE_NAME` | `invoice.odt`      | The name of the invoice template                                                                                                                            | `invoice.invoiceTemplateName` |
| `INVOICE_NAME`          | `_NO_-_HOUSEHOLD_` | The name of the generated invoice files. The default will give `<invoice number>-<household name>.pdf`, e.g. `313-Duck.pdf`. See below for more information | `invoice.invoiceName`         |

### Optional app configuration

The app can also be configured. This is because the app is started together with the server.

| Environment variable | Default value | Description                                                                                   |
|----------------------|---------------|-----------------------------------------------------------------------------------------------|
| `APP_PORT`           | `8000`        | The port that the app will be available at. Change this if the default port is already in use |

### Configure invoice template

The invoice template is an Open Document Text (ODT) file used as a template when generating PDF invoice files. The
template can be customized to include the information you want in the invoice. The template file must be placed in the
configuration bucket by executing the following command:

```shell
./s3.sh -cp <template file path> config
```

The name of the template file from `template file path` should be equal to `INVOICE_TEMPLATE_NAME` (se above).

Run `./s3.sh --help` to get help

See [example-templates](./example-templates) for examples of invoice templates.

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
| `_LI_T_P_`       | Total price of the invoice line                               |
| `_IN_T_P_`       | Total price of the invoice                                    |

### Invoice file name configuration

The following list shows the placeholders that can be used to create the invoice file name:

| Placeholder name | Description                                                  |
|------------------|--------------------------------------------------------------|
| `_NO_`           | Invoice number                                               |
| `_DATE_`         | Generated date                                               |
| `_DUE_DATE_`     | Due data                                                     |
| `_HOUSEHOLD_`    | Name of household                                            |
| `_ADDRESS1_`     | Address 1                                                    |
| `_NAME1_`        | First name and lastname of the first person in the household |
| `_PRODUCT1_`     | Name of the product in the first invoice line                |

All spaces will be removed when creating the invoice name. See `INVOICE_NAME` above for more information.

## Start and stop Simple Invoice

All the components will run in Docker.

You need to log in to Github Container Registry before you start:

```shell
docker login ghcr.io -u <your github username>
```

Go to the command shell and run Simple Invoice with the following command:

```shell
./start.sh
```

Add `--help` to the script to see all the options.

The Simple Invoice App can be reached at http://localhost:8000.

**Note!** The port might be different if you have set the `APP_PORT` environment variable.

Go to the command shell and run the following command to stop Simple invoice:

````shell
./stop.sh
````

## Maintain Simple Invoice

Storages for the database and files will be created by the server the first time Simple Invoice is run. The position of
the storages is determined by Docker.

### Manage files

You can use the `s3.sh` script to manage files in the S3-compatible storage.

Run the following command to get help:

```shell
./s3.sh --help
```

You can also open the MinIO Console at http://localhost:9001.

### Back up data

**Note!** Stop Simple Invoice before backing up the data.

Go to the command shell and run the following command to back up the database, invoice documents, and invoice templates:

```shell
./backup.sh
```

This will create a `tar.gz` file. The name will be written to the console.

Run the command with `--help` to get help.

**Note!** The `.env` file is not backup up, as it contains secret values. This file must be copied manually to a safe
place.

### Restore data

**Note!** Stop Simple Invoice before restoring the data.

Go to the command shell and run the following command to restore the database, invoice documents, and invoice templates:

```shell
./restore.sh <backup file to restore>
```

Run the command with `--help` to get help.

**WARNING!** Existing data might be overwritten when restoring! Which data, will be listed when running the command.

## Develop Simple Invoice Server

See [Simple Invoice Server Development](src/README.md).

Developed with [IntelliJ IDEA](https://www.jetbrains.com/idea/).

## Licence

[![License: GNU GPL v3.0](https://img.shields.io/badge/License-GNU%20GPL%20v3.0-brightgreen.svg)](https://choosealicense.com/licenses/gpl-3.0/)

## Contact

[dev@johannessenweb.com](mailto:dev@johannessenweb.com)
