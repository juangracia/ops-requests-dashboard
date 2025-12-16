#!/bin/bash

echo "Starting PostgreSQL database..."
docker-compose up -d

echo "Waiting for database to be ready..."
sleep 5

echo "Database is ready!"
echo ""
echo "Connection details:"
echo "  Host: localhost"
echo "  Port: 25432"
echo "  Database: opsrequests"
echo "  User: postgres"
echo "  Password: postgres"
echo ""
echo "To stop the database, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"
