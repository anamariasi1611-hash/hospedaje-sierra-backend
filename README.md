# Hospedaje Sierra Backend

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green)](https://spring.io/projects/spring-boot) [![Maven](https://img.shields.io/badge/Maven-3.8-blue)](https://maven.apache.org/) [![MySQL](https://img.shields.io/badge/MySQL-8.0-yellow)](https://www.mysql.com/) [![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

Backend API RESTful para el sistema de gestión hotelera "Hospedaje Sierra". Maneja autenticación JWT, reservas, habitaciones, compras, productos, observaciones y reportes PDF. Desarrollado en Java con Spring Boot para un proyecto formativo SENA.

## Introducción

Hospedaje Sierra Backend es la capa de servidor para una app de gestión hotelera. Permite a empleados/admin registrar huéspedes, manejar reservas, ventas adicionales y generar informes. Usa JWT para seguridad, JPA para DB, y OpenPDF para reportes.

Por qué este proyecto: Cumple con requerimientos de software funcional (CRUD reservas) y no funcional (seguridad, escalabilidad). Es un ejemplo real de API full-stack integrable con frontend React.

## Requisitos

- Java JDK 21+ (`java -version`).
- Maven 3.8+ (`mvn -v`).
- MySQL 8.0+ instalado (crea BD `hospedaje_sierra`).
- Git para clonar.
- Opcional: IntelliJ o VS Code para edición.
- RAM 2GB+ (para run local).

Tip: En Colombia, si usas ETB/Claro, descarga deps Maven con buena conexión – evita peak hours.

## Instalación/Setup

1. Clona el repo:
git clone https://github.com/anamariasi1611-hash/hospedaje-sierra-backend.git
cd hospedaje-sierra-backend
text2. Instala dependencias (Maven descarga auto):
mvn clean install
text3. Configura DB MySQL:
- Crea BD: `CREATE DATABASE hospedaje_sierra;`
- User: `CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppass'; GRANT ALL ON hospedaje_sierra.* TO 'appuser'@'localhost';`

## Configuración

1. Copia `application.properties.example` a `src/main/resources/application.properties` (ignorado en Git para seguridad).
2. Llena con tus valores (NO commitees – contiene secrets):
3. 
spring.datasource.url=jdbc:mysql://localhost:3306/hospedaje_sierra?useSSL=false&serverTimezone=America/Bogota
spring.datasource.username=[tu_user_ej_root]
spring.datasource.password=[tu_password_segura]
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=[tu_secret_jwt_largo_64chars_base64]
jwt.expiration=86400000  # 24h
logging.level.org.springframework.security=DEBUG
text- Razón liberal: Secrets locales – evita leaks. Genera JWT secret fuerte (usa online tool seguro, no hardcode).

5. Si profiles: Crea application-dev.properties para dev.

## Cómo Correr

- Dev: `mvn spring-boot:run` (localhost:8080).
- Prod: Build JAR `mvn package`, run `java -jar target/hospedaje-sierra-backend-0.0.1-SNAPSHOT.jar`.
- Debug: En IntelliJ, run Application.java con breakpoints.

Tip: Error DB? Chequea logs – "Access denied" = creds wrong. CORS? WebConfig permite frontend ports.
