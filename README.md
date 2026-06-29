# Admin Tools Backend

Backend base en Spring Boot con arquitectura hexagonal.

## Estructura

```
src/main/java/com/admintools/
├── AdminToolsBackendApplication.java
├── application/
│   ├── dto/
│   └── usecase/
├── domain/
│   ├── model/
│   └── port/
└── infrastructure/
    ├── config/
    ├── external/
    ├── persistence/
    └── rest/
```

## Requisitos

- Java 17
- Maven 3.9+

## Ejecucion local

```bash
./mvnw spring-boot:run
```

o

```bash
mvn spring-boot:run
```

## Docker

```bash
docker build -t admin-tools-backend .
docker run -p 8080:8080 admin-tools-backend
```
