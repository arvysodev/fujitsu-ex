package ee.trialtask.delivery.weather.config;

import ee.trialtask.delivery.weather.domain.City;

public record ConfiguredWeatherStation(
        City city,
        String name,
        String wmoCode
) {
}
