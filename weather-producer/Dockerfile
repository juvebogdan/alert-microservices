# Start with a base image containing Java runtime
FROM eclipse-temurin:21-jdk

# Information about who maintains the image
LABEL maintainer="krivokapic.bogdan10@gmail.com"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Set application's JAR file
ARG JAR_FILE=target/weather-producer-0.0.1-SNAPSHOT.jar

# Add the application's JAR to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
