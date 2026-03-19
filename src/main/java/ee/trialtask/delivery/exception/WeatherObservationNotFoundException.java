package ee.trialtask.delivery.exception;

import ee.trialtask.delivery.weather.domain.City;

public class WeatherObservationNotFoundException extends RuntimeException {

    public WeatherObservationNotFoundException(City city) {
        super("No weather observation found for city: " + city);
    }
}
