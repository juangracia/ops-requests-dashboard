#!/bin/bash
echo "Running backend tests..."
cd backend && mvn test
echo "Running frontend lint..."
cd ../frontend && npm run lint
