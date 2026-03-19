package ee.trialtask.delivery.weather.client;

import ee.trialtask.delivery.weather.client.dto.ObservationsResponse;
import ee.trialtask.delivery.weather.config.WeatherProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherApiClient {

    private final RestClient restClient;
    private final WeatherProperties properties;

    public WeatherApiClient(WeatherProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    public ObservationsResponse fetchObservations() {
        return restClient.get()
                .uri(properties.api().url())
                .retrieve()
                .body(ObservationsResponse.class);
    }
}
