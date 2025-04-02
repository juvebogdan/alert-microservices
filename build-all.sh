#!/bin/bash

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven or fix the Maven wrapper."
    exit 1
fi

echo "Building Weather Producer service..."
cd weather-producer
mvn clean package -DskipTests
cd ..

echo "Building Weather Analyzer service..."
cd weather-analyzer
mvn clean package -DskipTests
cd ..

echo "Building Alert Notification service..."
cd alert-notification
mvn clean package -DskipTests
cd ..

echo "All services built successfully!"
echo "You can now run 'docker-compose up -d' to start the system."
