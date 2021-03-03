FROM openjdk:8-jre-alpine
WORKDIR /app
COPY target/assetto-corsa-telemetry-service-*.jar ./acts.jar
ENTRYPOINT [ "java", "-jar", "acts.jar" ]