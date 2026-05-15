#!/bin/bash

mode -e

echo "Gracefully shutting down Simple Invoice..."

docker compose --progress=plain stop server app
sleep 5

docker compose --progress=plain stop postgres-server

# If postgres is still running, force it down
if docker compose ps postgres-server | grep -q "running"; then
    echo "PostgreSQL still running, forcing graceful shutdown..."
    docker compose exec postgres-server pg_ctl stop -D /var/lib/postgresql/data -m fast
    sleep 10
fi

# Finally bring everything down
docker compose --progress=plain down

echo "Simple Invoice has shut down!"
