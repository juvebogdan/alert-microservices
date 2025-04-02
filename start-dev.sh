#!/bin/bash

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to check if Docker is running
check_docker() {
  echo -e "${YELLOW}Checking if Docker is running...${NC}"
  if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running. Please start Docker Desktop and try again.${NC}"
    exit 1
  fi
  echo -e "${GREEN}Docker is running.${NC}"
}

# Function to read environment variables from .env file
load_env_file() {
  echo -e "${YELLOW}Loading environment variables from .env file...${NC}"
  if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
    echo -e "${GREEN}Environment variables loaded.${NC}"
  else
    echo -e "${RED}Warning: .env file not found. OpenWeatherMap API key might not be set.${NC}"
  fi
}

# Function to create application properties files with correct configurations
create_property_files() {
  echo -e "${YELLOW}Creating development property files...${NC}"
  
  # Weather Producer dev properties
  mkdir -p weather-producer/src/main/resources
  cat > weather-producer/src/main/resources/application-dev.properties << EOF
server.port=8080
spring.application.name=weather-producer
spring.kafka.bootstrap-servers=localhost:9092
openweathermap.api.key=${OPENWEATHERMAP_API_KEY}
EOF

  # Weather Analyzer dev properties
  mkdir -p weather-analyzer/src/main/resources
  cat > weather-analyzer/src/main/resources/application-dev.properties << EOF
server.port=8081
spring.application.name=weather-analyzer
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=weather-analyzer-group
spring.kafka.consumer.auto-offset-reset=earliest
EOF

  # Alert Notification dev properties
  mkdir -p alert-notification/src/main/resources
  cat > alert-notification/src/main/resources/application-dev.properties << EOF
server.port=8082
spring.application.name=alert-notification
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=alert-notification-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.datasource.url=jdbc:postgresql://localhost:5432/weatheralerts
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
EOF

  echo -e "${GREEN}Property files created.${NC}"
}

# Start the infrastructure containers
start_infrastructure() {
  echo -e "${YELLOW}Starting infrastructure containers (Kafka, Zookeeper, PostgreSQL)...${NC}"
  docker-compose -f docker-compose-dev.yml down
  docker-compose -f docker-compose-dev.yml up -d

  # Wait for containers to be healthy
  echo -e "${YELLOW}Waiting for containers to be ready...${NC}"
  
  # Wait for PostgreSQL
  echo -n "Waiting for PostgreSQL to start"
  until docker exec postgres pg_isready -U postgres > /dev/null 2>&1; do
    echo -n "."
    sleep 2
  done
  echo -e "\n${GREEN}PostgreSQL is ready.${NC}"
  
  # Wait a bit for Kafka to be ready (simplistic approach)
  echo -n "Waiting for Kafka to start"
  for i in {1..30}; do
    echo -n "."
    sleep 1
  done
  echo -e "\n${GREEN}Kafka should be ready now.${NC}"
  
  echo -e "${GREEN}Infrastructure is up and running.${NC}"
}

# Create batch files to run each service
create_run_scripts() {
  echo -e "${YELLOW}Creating run scripts for each service...${NC}"
  
  # Weather Producer batch file
  cat > run-producer.bat << EOF
@echo off
cd weather-producer
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
pause
EOF

  # Weather Analyzer batch file
  cat > run-analyzer.bat << EOF
@echo off
cd weather-analyzer
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
pause
EOF

  # Alert Notification batch file
  cat > run-notification.bat << EOF
@echo off
cd alert-notification
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
pause
EOF

  # Make all scripts executable
  chmod +x run-producer.bat run-analyzer.bat run-notification.bat
  
  echo -e "${GREEN}Run scripts created.${NC}"
}

# Print instructions for running services
print_run_instructions() {
  echo -e "${YELLOW}=======================================${NC}"
  echo -e "${YELLOW}To run the services, open three separate terminal windows and run:${NC}"
  echo -e "${GREEN}Window 1: ./run-producer.bat${NC}"
  echo -e "${GREEN}Window 2: ./run-analyzer.bat${NC}"
  echo -e "${GREEN}Window 3: ./run-notification.bat${NC}"
  echo -e "${YELLOW}=======================================${NC}"
}

# Function to display help information
show_help() {
  echo -e "${YELLOW}Weather Alert System - Development Script${NC}"
  echo -e "This script sets up a local development environment for the Weather Alert System."
  echo -e "Usage:"
  echo -e "  ./start-dev.sh              Start the development environment"
  echo -e "  ./start-dev.sh stop         Stop all containers but keep data volumes"
  echo -e "  ./start-dev.sh clean        Stop and remove all containers and data volumes"
  echo -e "  ./start-dev.sh help         Show this help message"
}

# Function to stop infrastructure
stop_infrastructure() {
  echo -e "${YELLOW}Stopping infrastructure containers...${NC}"
  docker-compose -f docker-compose-dev.yml down
  echo -e "${GREEN}Infrastructure containers have been stopped.${NC}"
}

# Function to completely clean up
clean_environment() {
  echo -e "${YELLOW}Stopping containers and removing volumes...${NC}"
  docker-compose -f docker-compose-dev.yml down -v
  echo -e "${GREEN}Containers and volumes have been removed.${NC}"
}

# Check command line arguments
case "$1" in
  stop)
    stop_infrastructure
    exit 0
    ;;
  clean)
    clean_environment
    exit 0
    ;;
  help)
    show_help
    exit 0
    ;;
esac

# Main execution
clear
echo -e "${YELLOW}=======================================${NC}"
echo -e "${GREEN}Weather Alert System - Development Setup${NC}"
echo -e "${YELLOW}=======================================${NC}"

check_docker
load_env_file
create_property_files
start_infrastructure
create_run_scripts
print_run_instructions

echo -e "${YELLOW}=======================================${NC}"
echo -e "${GREEN}Infrastructure is now running.${NC}"
echo -e "${YELLOW}To stop infrastructure: ./start-dev.sh stop${NC}"
echo -e "${YELLOW}To clean everything: ./start-dev.sh clean${NC}"
echo -e "${YELLOW}=======================================${NC}"
