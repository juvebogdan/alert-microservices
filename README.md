# Weather Alert System

This project is a distributed system for collecting, analyzing, and sending notifications for weather alerts. It consists of three microservices:

1. **Weather Producer**: Collects weather data from OpenWeatherMap API and publishes it to Kafka
2. **Weather Analyzer**: Processes weather data and generates alerts based on conditions
3. **Alert Notification**: Receives alerts and sends notifications through various channels

## System Architecture

- **Kafka**: Message broker for communication between services
- **PostgreSQL**: Database for storing alert history
- **Docker**: Containerization for all services and infrastructure

## Prerequisites

- Java 21
- Maven
- Docker and Docker Compose
- OpenWeatherMap API key

## Local Development Setup

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/weather-alert-system.git
cd weather-alert-system
```

### 2. Set up environment variables

Create a `.env` file in the root directory with your OpenWeatherMap API key:

```
OPENWEATHERMAP_API_KEY=your_api_key_here
```

### 3. Start the development infrastructure

The project includes a script to set up the development environment:

```bash
# Make the script executable
chmod +x start-dev.sh

# Run the script
./start-dev.sh
```

This script will:

- Start Kafka, Zookeeper, and PostgreSQL in Docker containers
- Create necessary development properties files
- Generate run scripts for each service

### 4. Run the services

Open three separate terminal windows and run each service:

#### Terminal 1 - Weather Producer:

```bash
./run-producer.bat    # Windows
# OR
cd weather-producer
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev    # Linux/Mac
```

#### Terminal 2 - Weather Analyzer:

```bash
./run-analyzer.bat    # Windows
# OR
cd weather-analyzer
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev    # Linux/Mac
```

#### Terminal 3 - Alert Notification:

```bash
./run-notification.bat    # Windows
# OR
cd alert-notification
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev    # Linux/Mac
```

### 5. Stop the development environment

To stop the infrastructure services:

```bash
./start-dev.sh stop
```

To clean up everything including data volumes:

```bash
./start-dev.sh clean
```

## Docker Deployment

### 1. Build all services

The project includes a script to build all services:

```bash
# Make the script executable
chmod +x build-all.sh

# Run the script
./build-all.sh
```

This will build all three services using Maven.

### 2. Start the full system with Docker Compose

Make sure you have the `.env` file with your OpenWeatherMap API key, then:

```bash
docker-compose up -d
```

This will:

- Start Kafka, Zookeeper, and PostgreSQL
- Build and start all three microservices
- Set up proper networking between containers

### 3. Check service logs

```bash
# View logs for all services
docker-compose logs -f

# View logs for a specific service
docker-compose logs -f weather-producer
docker-compose logs -f weather-analyzer
docker-compose logs -f alert-notification
```

### 4. Stop the system

```bash
docker-compose down
```

To remove all data volumes as well:

```bash
docker-compose down -v
```

## API Endpoints

### Weather Producer

- `POST /api/weather` - Manually submit weather data

### Alert Notification

- `GET /api/alerts` - Get recent alerts
- `GET /api/alerts/location/{locationId}` - Get alerts by location
- `GET /api/alerts/type/{alertType}` - Get alerts by alert type

## Monitoring

To check the system is working properly:

1. Check the logs to see weather data being collected and processed
2. Query the alert notification service endpoints to see generated alerts
3. The Alert Notification service will log the notifications it would send

## Troubleshooting

- If services fail to start, check that Kafka and PostgreSQL are running
- Ensure your OpenWeatherMap API key is correctly set in the `.env` file
- Check the logs of each service for specific errors

## Development Notes

- The system uses Spring Boot 3.4.4 and requires Java 21
- The Weather Producer fetches random weather data every 5 minutes
- The Alert Notification service would send emails, SMS, and push notifications in a production environment
