#!/bin/bash
echo "Starting development environment..."
docker compose up -d postgres
echo "Waiting for PostgreSQL..."
sleep 5
echo "PostgreSQL ready. Start backend and frontend manually or use docker compose up"
