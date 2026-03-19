package ee.trialtask.delivery.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "app.weather")
public record WeatherProperties(
        WeatherApiProperties api,
        Map<String, WeatherStationProperties> stations
) {
}
