package ee.trialtask.delivery.weather.service;

import ee.trialtask.delivery.weather.client.WeatherApiClient;
import ee.trialtask.delivery.weather.client.dto.ObservationsResponse;
import ee.trialtask.delivery.weather.client.dto.StationDto;
import ee.trialtask.delivery.weather.config.ConfiguredWeatherStation;
import ee.trialtask.delivery.weather.config.WeatherStationResolver;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import ee.trialtask.delivery.weather.mapper.WeatherMapper;
import ee.trialtask.delivery.weather.repository.WeatherObservationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherImportService {

    private final WeatherApiClient weatherApiClient;
    private final WeatherStationResolver weatherStationResolver;
    private final WeatherMapper weatherMapper;
    private final WeatherObservationRepository weatherObservationRepository;

    public WeatherImportService(
            WeatherApiClient weatherApiClient,
            WeatherStationResolver weatherStationResolver,
            WeatherMapper weatherMapper,
            WeatherObservationRepository weatherObservationRepository
    ) {
        this.weatherApiClient = weatherApiClient;
        this.weatherStationResolver = weatherStationResolver;
        this.weatherMapper = weatherMapper;
        this.weatherObservationRepository = weatherObservationRepository;
    }

    @Transactional
    public int importLatestObservations() {
        ObservationsResponse response = weatherApiClient.fetchObservations();
        List<StationDto> stationDtos = response.stations();

        if (stationDtos == null || stationDtos.isEmpty()) {
            return 0;
        }

        Map<String, StationDto> stationByWmoCode = indexStationsByWmoCode(stationDtos);
        LocalDateTime importedAt = LocalDateTime.now();

        int importedCount = 0;

        for (ConfiguredWeatherStation configuredStation : weatherStationResolver.getConfiguredStations()) {
            StationDto stationDto = stationByWmoCode.get(configuredStation.wmoCode());

            if (stationDto == null) {
                continue;
            }

            if (!configuredStation.name().equals(stationDto.name())) {
                continue;
            }

            WeatherObservation weatherObservation =
                    weatherMapper.toEntity(stationDto, configuredStation.city(), importedAt);

            boolean alreadyExists = weatherObservationRepository.existsByWmoCodeAndObservationTimestamp(
                    weatherObservation.getWmoCode(),
                    weatherObservation.getObservationTimestamp()
            );

            if (alreadyExists) {
                continue;
            }

            try {
                weatherObservationRepository.saveAndFlush(weatherObservation);
                importedCount++;
            } catch (DataIntegrityViolationException ignored) {
            }
        }

        return importedCount;
    }

    private Map<String, StationDto> indexStationsByWmoCode(List<StationDto> stationDtos) {
        Map<String, StationDto> stationByWmoCode = new HashMap<>();

        for (StationDto stationDto : stationDtos) {
            if (stationDto.wmoCode() == null || stationDto.wmoCode().isBlank()) {
                continue;
            }

            stationByWmoCode.put(stationDto.wmoCode(), stationDto);
        }

        return stationByWmoCode;
    }
}
