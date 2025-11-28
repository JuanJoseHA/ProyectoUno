# Compilar el proyecto en un JAR ejecutable
FROM maven:3.9.11-eclipse-temurin-21 AS build
# Directorio de trabajo dentro del contenedor de construcción
WORKDIR /home/app/
# Copia todo el código fuente al contenedor
COPY . /home/app/
# Compila el proyecto y genera el JAR final (omitiendo tests)
RUN mvn clean package -DskipTests

# Crear la imagen ligera de ejecución
# Usa la imagen base de JRE 21 más ligera
FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=ProyectoUno-0.0.1-SNAPSHOT.jar 

# Directorio de la aplicación en el contenedor final
WORKDIR /app 

# Copia el JAR generado en la etapa de construcción
COPY --from=build /home/app/target/${JAR_FILE} /app/app.jar

# Copia las imágenes estáticas (del menú) para que estén disponibles.
COPY --from=build /home/app/src/main/resources/static/imagenes /app/BOOT-INF/classes/static/imagenes

# Puerto en el que corre la aplicación (8080 en application.properties)
EXPOSE 8080

# Comando para iniciar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]