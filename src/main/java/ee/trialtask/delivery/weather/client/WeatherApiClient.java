package ee.trialtask.delivery.weather.client;

import ee.trialtask.delivery.weather.client.dto.ObservationsResponse;
import ee.trialtask.delivery.weather.config.WeatherProperties;
import ee.trialtask.delivery.exception.WeatherDataFetchException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WeatherApiClient {

    private final RestClient restClient;
    private final WeatherProperties properties;

    public WeatherApiClient(RestClient weatherRestClient, WeatherProperties properties) {
        this.restClient = weatherRestClient;
        this.properties = properties;
    }

    /**
     * Fetches the latest weather observations from the external weather API.
     *
     * @return the weather observations response returned by the external API
     * @throws WeatherDataFetchException
     *         if the external API request fails or returns an empty response body
     */
    public ObservationsResponse fetchObservations() {
        try {
            ObservationsResponse response = restClient.get()
                    .uri(properties.api().url())
                    .retrieve()
                    .body(ObservationsResponse.class);

            if (response == null) {
                throw new WeatherDataFetchException("Weather API returned empty response body");
            }

            return response;
        } catch (RestClientException exception) {
            throw new WeatherDataFetchException("Failed to fetch weather observations", exception);
        }
    }
}
