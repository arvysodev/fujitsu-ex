# Delivery Fee Calculator

REST API for calculating courier delivery fees based on city, vehicle type and weather observations.

---

## Overview

This application calculates delivery fees for couriers in Estonian cities based on:

* **Regional base fee** (city + vehicle type)
* **Weather conditions** (temperature, wind speed, phenomenon)

Weather data is periodically imported from the Estonian Environment Agency XML API and stored in a database.
The calculation endpoint uses stored data and is not dependent on external API availability.

---

## Architecture

The Application is divided into two main flows:

### 1. Weather Data Import

* A scheduled job fetches weather data from an external XML API
* XML is deserialized into DTO objects 
* Only configured stations (Tallinn, Tartu, Pärnu) are selected 
* Data is mapped to internal domain model (WeatherObservation)
* Observations are stored in the database as historical records (no overwriting)

### 2. Delivery Fee Calculation

* REST endpoint receives request parameters (city, vehicleType, optional dateTime)
* Service retrieves relevant weather observation:
  * Latest observation (default)
  * Or latest observation at or before given dateTime
* Delivery fee is calculated as:
  * totalFee = regionalBaseFee + weatherExtraFee
* If weather conditions forbid the selected vehicle, an error is returned

---

## Technologies

* Java 21 
* Spring Boot 
* Spring Web 
* Spring Data JPA 
* H2 Database 
* MapStruct 
* OpenAPI (Swagger)
* Docker (optional)

---

## Running the Application

### 1. Run locally

```
./gradlew bootRun
```

Application will start on:

```
http://localhost:8080
```

---

### 2. Run with Docker

Build the Docker image:

```
docker build -t delivery-fee-calculator .
```

Run the container:

```
docker run -p 8080:8080 delivery-fee-calculator
```

Application will be available on:

```
http://localhost:8080
```

---

### 3. Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

### 4. H2 Console

```
http://localhost:8080/h2-console
```

Default settings:

* JDBC URL: jdbc:h2:mem:delivery_fee_db
* Username: sa
* Password: *(empty)*

---

## API

### Calculate Delivery Fee

### GET ```/api/v1/delivery-fee```

**Query parameters**

* city - required 
* vehicleType - required
* dateTime - optional (ISO datetime for historical calculation)

**Example**

```GET /api/v1/delivery-fee?city=TALLINN&vehicleType=BIKE```

**With historical timestamp**

```GET /api/v1/delivery-fee?city=TARTU&vehicleType=SCOOTER&dateTime=2024-01-01T10:00:00```

---

## Weather Data Source

Data is fetched from:

```https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php```

---

## Configuration

Weather stations are configured via ```application.yml```:

```
app: 
    weather: 
        stations: 
            tallinn: 
                name: Tallinn-Harku 
                wmo-code: "26038" 
            tartu: 
                name: Tartu-Tõravere 
                wmo-code: "26242" 
            parnu: 
                name: Pärnu 
                wmo-code: "41803"
```

New stations can be added by extending this configuration and updating the ```City``` enum.

---

## Error handling

The API uses RFC 7807 Problem Details format.

Example:

```
{
    "type": "https://delivery-fee-calculator/problems/vehicle-usage-forbidden",
    "title": "Vehicle Usage Forbidden",
    "status": 400,
    "detail": "Usage of selected vehicle type is forbidden",
    "instance": "/api/v1/delivery-fee"
}
```

---

## Design Decisions

* Separation of concerns: import and calculation are independent
* No direct dependency on external API during fee calculation
* Historical data is preserved (no overwriting)
* Config-driven station selection
* Basic idempotency ensured via database constraints and duplicate handling

### Scalability Considerations

The application is designed with separation of concerns in mind:

- Weather data ingestion is decoupled from fee calculation
- External API integration is isolated from business logic
- Station configuration is externalized via application properties
- Historical data storage enables future analytics or extended features

This allows independent evolution of system components and simplifies future extensions.

---

## Testing

Run tests:

```
./gradlew test
```
