FROM openjdk:17-slim
WORKDIR /app
COPY target/quota-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=cloud
ENTRYPOINT ["java", "-jar", "app.jar"]