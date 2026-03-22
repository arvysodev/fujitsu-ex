package ee.trialtask.delivery.fee.service;

import ee.trialtask.delivery.exception.ForbiddenVehicleUsageException;
import ee.trialtask.delivery.exception.WeatherObservationNotFoundException;
import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.fee.dto.DeliveryFeeResponse;
import ee.trialtask.delivery.weather.domain.City;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import ee.trialtask.delivery.weather.repository.WeatherObservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryFeeCalculationServiceTest {

    @InjectMocks
    private DeliveryFeeCalculationService deliveryFeeCalculationService;

    @Mock
    private WeatherExtraFeeService weatherExtraFeeService;

    @Mock
    private RegionalBaseFeeService regionalBaseFeeService;

    @Mock
    private WeatherObservationRepository weatherObservationRepository;

    @Test
    void calculate_withDatetimeNotProvided_shouldReturnFee() {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.SCOOTER;

        WeatherObservation weatherObservation = new WeatherObservation(
                "Tallinn-Harku",
                "26038",
                city,
                BigDecimal.valueOf(2.7),
                BigDecimal.valueOf(1.8),
                "Light rain",
                LocalDateTime.of(2026, 3, 20, 12, 0),
                LocalDateTime.of(2026, 3, 20, 12, 1)
        );

        when(weatherObservationRepository.findTopByCityOrderByObservationTimestampDesc(city))
                .thenReturn(Optional.of(weatherObservation));
        when(weatherExtraFeeService.calculate(weatherObservation, vehicleType))
                .thenReturn(BigDecimal.valueOf(0.5));
        when(regionalBaseFeeService.calculate(city, vehicleType))
                .thenReturn(BigDecimal.valueOf(3.5));

        DeliveryFeeResponse result = deliveryFeeCalculationService.calculate(city, vehicleType, null);

        assertThat(result.city()).isEqualTo(city);
        assertThat(result.vehicleType()).isEqualTo(vehicleType);
        assertThat(result.fee()).isEqualByComparingTo(BigDecimal.valueOf(4.0));

        verify(weatherObservationRepository).findTopByCityOrderByObservationTimestampDesc(city);
        verify(regionalBaseFeeService).calculate(city, vehicleType);
        verify(weatherExtraFeeService).calculate(weatherObservation, vehicleType);
        verifyNoMoreInteractions(weatherObservationRepository, regionalBaseFeeService, weatherExtraFeeService);
    }

    @Test
    void calculate_withDatetimeProvided_shouldReturnFee() {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.SCOOTER;
        LocalDateTime requestedDateTime = LocalDateTime.of(2026, 3, 20, 12, 3);

        WeatherObservation weatherObservation = new WeatherObservation(
                "Tallinn-Harku",
                "26038",
                city,
                BigDecimal.valueOf(2.7),
                BigDecimal.valueOf(1.8),
                "Light rain",
                LocalDateTime.of(2026, 3, 20, 12, 0),
                LocalDateTime.of(2026, 3, 20, 12, 5)
        );

        when(weatherObservationRepository
                .findTopByCityAndObservationTimestampLessThanEqualOrderByObservationTimestampDesc(
                        city,
                        requestedDateTime
                ))
                .thenReturn(Optional.of(weatherObservation));
        when(weatherExtraFeeService.calculate(weatherObservation, vehicleType))
                .thenReturn(BigDecimal.valueOf(0.5));
        when(regionalBaseFeeService.calculate(city, vehicleType))
                .thenReturn(BigDecimal.valueOf(3.5));

        DeliveryFeeResponse result = deliveryFeeCalculationService.calculate(
                city,
                vehicleType,
                requestedDateTime
        );

        assertThat(result.city()).isEqualTo(city);
        assertThat(result.vehicleType()).isEqualTo(vehicleType);
        assertThat(result.fee()).isEqualByComparingTo(BigDecimal.valueOf(4.0));

        verify(weatherObservationRepository)
                .findTopByCityAndObservationTimestampLessThanEqualOrderByObservationTimestampDesc(
                        city,
                        requestedDateTime
                );
        verify(weatherObservationRepository, never()).findTopByCityOrderByObservationTimestampDesc(city);
        verify(weatherExtraFeeService).calculate(weatherObservation, vehicleType);
        verify(regionalBaseFeeService).calculate(city, vehicleType);
        verifyNoMoreInteractions(weatherObservationRepository, regionalBaseFeeService, weatherExtraFeeService);
    }

    @Test
    void calculate_withDateTimeNotProvidedAndWeatherObservationNotFound_shouldThrowException() {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.SCOOTER;

        when(weatherObservationRepository.findTopByCityOrderByObservationTimestampDesc(city))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryFeeCalculationService.calculate(city, vehicleType, null))
                .isInstanceOf(WeatherObservationNotFoundException.class)
                .hasMessage("No weather observation found for city: %s".formatted(city.name()));

        verify(weatherObservationRepository).findTopByCityOrderByObservationTimestampDesc(city);
        verifyNoMoreInteractions(weatherObservationRepository);
    }

    @Test
    void calculate_withDatetimeProvidedAndWeatherObservationNotFound_shouldThrowException() {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.SCOOTER;
        LocalDateTime requestedDateTime = LocalDateTime.of(2026, 3, 20, 12, 3);

        when(weatherObservationRepository
                .findTopByCityAndObservationTimestampLessThanEqualOrderByObservationTimestampDesc(
                        city,
                        requestedDateTime
                ))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryFeeCalculationService.calculate(
                city,
                vehicleType,
                requestedDateTime
        ))
                .isInstanceOf(WeatherObservationNotFoundException.class)
                .hasMessage("No weather observation found for city: %s".formatted(city.name()));

        verify(weatherObservationRepository)
                .findTopByCityAndObservationTimestampLessThanEqualOrderByObservationTimestampDesc(
                        city,
                        requestedDateTime
                );
        verify(weatherObservationRepository, never()).findTopByCityOrderByObservationTimestampDesc(city);
        verifyNoMoreInteractions(weatherObservationRepository);
    }

    @Test
    void calculate_whenWeatherExtraFeeServiceThrowsForbidden_shouldThrowException() {
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.SCOOTER;

        WeatherObservation weatherObservation = new WeatherObservation(
                "Tallinn-Harku",
                "26038",
                city,
                BigDecimal.valueOf(2.7),
                BigDecimal.valueOf(1.8),
                "Hail",
                LocalDateTime.of(2026, 3, 20, 12, 0),
                LocalDateTime.of(2026, 3, 20, 12, 5)
        );

        when(weatherObservationRepository.findTopByCityOrderByObservationTimestampDesc(city))
                .thenReturn(Optional.of(weatherObservation));
        when(regionalBaseFeeService.calculate(city, vehicleType))
                .thenReturn(BigDecimal.valueOf(3.5));
        when(weatherExtraFeeService.calculate(weatherObservation, vehicleType))
                .thenThrow(new ForbiddenVehicleUsageException("Usage of selected vehicle type is forbidden"));

        assertThatThrownBy(() -> deliveryFeeCalculationService.calculate(city, vehicleType, null))
                .isInstanceOf(ForbiddenVehicleUsageException.class)
                .hasMessage("Usage of selected vehicle type is forbidden");

        verify(weatherObservationRepository).findTopByCityOrderByObservationTimestampDesc(city);
        verify(regionalBaseFeeService).calculate(city, vehicleType);
        verify(weatherExtraFeeService).calculate(weatherObservation, vehicleType);
        verifyNoMoreInteractions(weatherObservationRepository, regionalBaseFeeService, weatherExtraFeeService);
    }
}
