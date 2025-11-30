#!/bin/bash

echo "Starting CHRMS Backend..."
echo "=========================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Start services
echo "Starting PostgreSQL and Redis..."
docker-compose up -d postgres redis

echo "Waiting for database to be ready..."
sleep 10

echo "Building and starting application..."
docker-compose up -d app

echo ""
echo "=========================="
echo "CHRMS Backend is starting!"
echo "=========================="
echo ""
echo "API: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/api/v1/swagger-ui.html"
echo ""
echo "Test Accounts:"
echo "  Patient:  patient1@test.com / password123"
echo "  Doctor:   doctor1@test.com / password123"
echo "  Admin:    admin@chrms.vn / password123"
echo ""
echo "To view logs: docker-compose logs -f app"
echo "To stop: docker-compose down"
echo "=========================="
