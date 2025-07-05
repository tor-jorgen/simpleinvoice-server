# Simple Invoice Server

This is the server for the Simple Invoice project. It provides an HTTP API that is used by the Simple Invoice App.

The server is built using [Ktor](https://ktor.io).

## Server configuration

| Property (`allication.yaml`) | Environment variable   | Default value                 | Description                                                                                                                 |
|------------------------------|------------------------|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `ktor.deployment.port`       | `SERVER_PORT`          | `8080`                        | The port the server runs at                                                                                                 |     
| `db.connectionPrefix`        | `DB_CONNECTION_PRE`    | `jdbc:postgresql://localhost` | The prefix for the database connection string. This includes the string up to (but not including) the colon before the port |     
| `db.port`                    | `DB_PORT`              | `5432`                        | The port the database server runs at                                                                                        |     
| `db.name`                    | `DB_NAME`              | `simple_invoice`              | The name of the database                                                                                                    |
| `db.user`                    | `DB_USER`              |                               | The name of the user used to connect to the database                                                                        |     
| `db.password`                | `DB_PASSWORD`          |                               | The password for the user used to connect to the database                                                                   |     
| `cfg.batch`                  | `BATCH_CONFIG`         | `./config/batch.yml`          | Path to batch configuration file                                                                                            |     
| `cfg.invoice`                | `INVOICE_CONFIG`       | `./config/config.yml`         | Path to invoice configuration file                                                                                          |     
| `security.clientId`          | `GOOGLE_CLIENT_ID`     |                               | OAuth 2 client ID (not yet in use)                                                                                          |     
| `security.clientSecret`      | `GOOGLE_CLIENT_SECRET` |                               | OAuth 2 client secret  (not yet in use)                                                                                     |
|                              | `CFG_PATH`             | `./config`                    | The path to the directory where the configuration files are stored                                                          |     
|                              | `DATA_PATH`            | `./data`                      | The path to the directory where the data files are stored                                                                   |     
|                              | `DB_DATA_PATH`         | `./data/postgres`             | The path to the directory where the database files are stored                                                               |     

Set the environment variables in the `.env` file. This file is use when running the server backend from Docker. E.g.:

    SERVER_PORT=8080
    CFG_PATH=./config
    DATA_PATH=./data
    DB_DATA_PATH=./data/postgres
    DB_CONNECTION_PRE=jdbc:postgresql://host.docker.internal
    DB_PORT=5432
    DB_NAME=simple_invoice
    DB_USER=user
    DB_PASSWORD=password
    BATCH_CONFIG=./config/batch.yml
    INVOICE_CONFIG=./config/config.yml
    GOOGLE_CLIENT_ID=XXX
    GOOGLE_CLIENT_SECRET=YYY

Note that the address to localhost is `host.docker.internal` when running in Docker. This is a special DNS name that
resolves to the internal IP address used by the host.

To list the environment variables with `localhost` as the address, you can run the following command:

```shell
./build.sh --list-env
```

These variables can be copied and pasted into the environment variables in IntelliJ, if you are running the server from
there.

## Document template configuration

The following table shows the name of the merge fields in the document template:

| Merge field name | Description                                                   |
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

## Database maintenance

Delete database:

```shell
sudo rm -rf data/postgres/
``` 

## Running locally

All the components of the backend will run in Docker. This includes the database and the server.

### Building the backend

The first time you run the backend, you need to build it. Do this by running the following command:

```shell
./build.sh
```  

Add `--help to see all options.`

### Running the backend

To run both the server and the database in Docker, use the following command:

```shell
./start.sh
```

Add `--help to see all options.`

Run the following command to stop the backend:

````shell
./stop.sh
````

## Debugging locally

The database will run in Docker, and the server will run in IntelliJ (or any other IDE).

1. Start the Postgres database in Docker:
    ```shell
   docker compose -f compose-postgres.yml up
    ```

2. Create a Ktor run configuration for `EngineMain` in IntelliJ
3. Paste the environment variables from above into the run configuration:
    1. Select _Edit environment variables_
    2. Click the past button
4. Run or debug the configuration

## Credits

Thanks to [Robin Selmer](https://github.com/robinselmer) for
the [Retro Error Page](https://codepen.io/robinselmer/pen/vJjbOZ)
