package ee.trialtask.delivery.weather.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "weather_observations")
public class WeatherObservation {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "station_name", nullable = false, length = 100)
    private String stationName;

    @Column(name = "wmo_code", nullable = false, length = 32)
    private String wmoCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "city", nullable = false, length = 32)
    private City city;

    @Column(name = "air_temperature", precision = 5, scale = 2)
    private BigDecimal airTemperature;

    @Column(name = "wind_speed", precision = 5, scale = 2)
    private BigDecimal windSpeed;

    @Column(name = "weather_phenomenon", length = 255)
    private String weatherPhenomenon;

    @Column(name = "observation_timestamp", nullable = false)
    private LocalDateTime observationTimestamp;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    protected WeatherObservation() {
    }

    public WeatherObservation(
            String stationName,
            String wmoCode,
            City city,
            BigDecimal airTemperature,
            BigDecimal windSpeed,
            String weatherPhenomenon,
            LocalDateTime observationTimestamp,
            LocalDateTime importedAt
    ) {
        this.stationName = stationName;
        this.wmoCode = wmoCode;
        this.city = city;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
        this.observationTimestamp = observationTimestamp;
        this.importedAt = importedAt;
    }

}
