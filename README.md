# Simple Invoice Server

This is the server for the Simple Invoice project. It provides an HTTP API that is used by the Simple Invoice App.

The server is built using [Ktor](https://ktor.io).

## Configuration

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

```bash
./start.sh --list-env
```

These variables can be copied and pasted into the environment variables in IntelliJ, if you are running the server from
there.

## Running locally

### All in Docker

To run both the server and the database in Docker, use the following command:

```bash
./start.sh
```

Run `--help to see all options.`

### Database in Docker, server in IntelliJ

1. Start the Postgres database in Docker:
    ```bash
   docker compose -f compose-postgres.yml up
    ```

2. Create a Ktor run configuration for `EngineMain` in IntelliJ
3. Paste the environment variables from above into the run configuration:
    1. Select _Edit environment variables_
    2. Click the past button

## Credits

Thanks to [Robin Selmer](https://github.com/robinselmer) for
the [Retro Error Page](https://codepen.io/robinselmer/pen/vJjbOZ)
