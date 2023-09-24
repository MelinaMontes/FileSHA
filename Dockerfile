
FROM openjdk:11-jre-slim

COPY target/demo-0.0.1-SNAPSHOT.war /app/app.war

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "app.war"]
