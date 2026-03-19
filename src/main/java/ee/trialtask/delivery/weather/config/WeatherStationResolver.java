package ee.trialtask.delivery.weather.config;

import ee.trialtask.delivery.weather.domain.City;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Getter
public class WeatherStationResolver {

    private final List<ConfiguredWeatherStation> configuredStations;

    public WeatherStationResolver(WeatherProperties properties) {
        this.configuredStations = properties.stations().entrySet().stream()
                .map(this::toConfiguredStation)
                .toList();
    }

    private ConfiguredWeatherStation toConfiguredStation(Map.Entry<String, WeatherStationProperties> entry) {
        City city = City.valueOf(entry.getKey().toUpperCase());
        WeatherStationProperties stationProperties = entry.getValue();

        return new ConfiguredWeatherStation(
                city,
                stationProperties.name(),
                stationProperties.wmoCode()
        );
    }
}
