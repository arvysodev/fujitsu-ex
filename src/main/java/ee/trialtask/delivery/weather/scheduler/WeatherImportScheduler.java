package ee.trialtask.delivery.weather.scheduler;

import ee.trialtask.delivery.weather.service.WeatherImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherImportScheduler {

    private final static Logger log =  LoggerFactory.getLogger(WeatherImportScheduler.class);

    private final WeatherImportService weatherImportService;

    public WeatherImportScheduler(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    /**
     * Triggers scheduled import of the latest weather observations.
     */
    @Scheduled(cron = "${app.weather.import-config.cron}")
    public void importWeatherObservations() {
        log.info("Starting scheduled weather observations import");

        long startedAt = System.currentTimeMillis();
        try {
            int importedCount = weatherImportService.importLatestObservations();
            long durationMs = System.currentTimeMillis() - startedAt;

            log.info(
                    "Scheduled weather observations import finished successfully. Imported {} new observations in {} ms",
                    importedCount,
                    durationMs
            );
        } catch (Exception ex) {
            long durationMs = System.currentTimeMillis() - startedAt;

            log.error(
                    "Scheduled weather observations import failed after {} ms",
                    durationMs,
                    ex
            );
        }
    }
}
