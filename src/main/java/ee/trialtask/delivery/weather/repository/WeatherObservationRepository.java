package ee.trialtask.delivery.weather.repository;

import ee.trialtask.delivery.weather.domain.City;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, UUID> {

    Optional<WeatherObservation> findTopByCityOrderByObservationTimestampDesc(City city);

    Optional<WeatherObservation> findTopByCityAndObservationTimestampLessThanEqualOrderByObservationTimestampDesc(
            City city,
            LocalDateTime observationTimestamp
    );

    boolean existsByWmoCodeAndObservationTimestamp(String wmoCode, LocalDateTime observationTimestamp);
}
