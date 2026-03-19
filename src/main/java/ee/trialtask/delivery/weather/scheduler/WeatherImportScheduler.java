package ee.trialtask.delivery.weather.scheduler;

import ee.trialtask.delivery.weather.config.WeatherProperties;
import ee.trialtask.delivery.weather.service.WeatherImportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherImportScheduler {

    private final WeatherImportService weatherImportService;

    public WeatherImportScheduler(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    @Scheduled(cron = "${app.weather.import-config.cron}")
    public void importWeatherObservations() {
        weatherImportService.importLatestObservations();
    }
}
