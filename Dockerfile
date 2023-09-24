# Utiliza una imagen base de Java
FROM openjdk:11-jre-slim

# Copia el archivo JAR de tu aplicación al contenedor
COPY target/demo-0.0.1-SNAPSHOT.war /app/app.war

# Establece el directorio de trabajo
WORKDIR /app

# Expone el puerto en el que se ejecuta tu aplicación
EXPOSE 3000

# Comando para ejecutar tu aplicación
CMD ["java", "-jar", "app.war"]
