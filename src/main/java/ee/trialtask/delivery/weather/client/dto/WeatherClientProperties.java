package ee.trialtask.delivery.weather.client.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.weather.client")
public record WeatherClientProperties(
        Duration connectTimeout,
        Duration readTimeout
) {
}
