package ee.trialtask.delivery.weather.mapper;

import ee.trialtask.delivery.weather.client.dto.StationDto;
import ee.trialtask.delivery.weather.domain.City;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stationName", source = "station.name")
    @Mapping(target = "wmoCode", source = "station.wmoCode")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "airTemperature", source = "station.airTemperature")
    @Mapping(target = "windSpeed", source = "station.windSpeed")
    @Mapping(target = "weatherPhenomenon", source = "station.phenomenon")
    @Mapping(target = "observationTimestamp", source = "station.observationTime")
    @Mapping(target = "importedAt", source = "importedAt")
    WeatherObservation toEntity(StationDto station, City city, LocalDateTime importedAt);

    default LocalDateTime map(String observationTime) {
        return OffsetDateTime.parse(observationTime).toLocalDateTime();
    }
}
