package ee.trialtask.delivery.fee.service;

import ee.trialtask.delivery.exception.ForbiddenVehicleUsageException;
import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.weather.domain.City;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class WeatherExtraFeeServiceTest {

    private WeatherExtraFeeService weatherExtraFeeService;

    @BeforeEach
    void setUp() {
        weatherExtraFeeService = new WeatherExtraFeeService();
    }

    @Test
    void calculate_withCar_shouldReturnZero() {
        WeatherObservation weatherObservation = weatherObservation(
                BigDecimal.valueOf(-15),
                BigDecimal.valueOf(25),
                "Thunder"
        );

        BigDecimal result = weatherExtraFeeService.calculate(weatherObservation, VehicleType.CAR);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @ParameterizedTest(name = "[{index}] vehicleType={0}. airTemperature={1}, expectedFee={2}")
    @MethodSource("temperatureExtraFeeCases")
    void calculate_withScooterAndBike_shouldReturnTemperatureExtraFee(
            VehicleType vehicleType,
            BigDecimal airTemperature,
            BigDecimal expectedFee
    ) {
        WeatherObservation weatherObservation = weatherObservation(
                airTemperature,
                BigDecimal.ZERO,
                "Clear"
        );

        BigDecimal result = weatherExtraFeeService.calculate(weatherObservation, vehicleType);

        assertThat(result).isEqualByComparingTo(expectedFee);
    }

    @ParameterizedTest(name = "[{index}] windSpeed={0}, expectedFee={1}")
    @MethodSource("bikeWindExtraFeeCases")
    void calculate_withBike_shouldReturnBikeWindExtraFee(
            BigDecimal windSpeed,
            BigDecimal expectedFee
    ) {
        WeatherObservation weatherObservation = weatherObservation(
                BigDecimal.ZERO,
                windSpeed,
                "Clear"
        );

        BigDecimal result = weatherExtraFeeService.calculate(weatherObservation, VehicleType.BIKE);

        assertThat(result).isEqualByComparingTo(expectedFee);
    }

    @ParameterizedTest(name = "[{index}] windSpeed={0}")
    @MethodSource("forbiddenBikeWindSpeedCases")
    void calculate_withBikeAndWindSpeedAboveTwenty_shouldThrowException(
            BigDecimal windSpeed
    ) {
        WeatherObservation weatherObservation = weatherObservation(
                BigDecimal.ZERO,
                windSpeed,
                "Clear"
        );

        assertThatThrownBy(() -> weatherExtraFeeService.calculate(weatherObservation, VehicleType.BIKE))
                .isInstanceOf(ForbiddenVehicleUsageException.class)
                .hasMessage("Usage of selected vehicle type is forbidden");
    }

    @ParameterizedTest(name = "[{index}] vehicleType={0}, phenomenon={1}, expectedFee={2}")
    @MethodSource("weatherPhenomenonExtraFeeCases")
    void calculate_withScooterAndBike_shouldReturnWeatherPhenomenonExtraFee(
            VehicleType vehicleType,
            String weatherPhenomenon,
            BigDecimal expectedFee
    ) {
        WeatherObservation weatherObservation = weatherObservation(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                weatherPhenomenon
        );

        BigDecimal result = weatherExtraFeeService.calculate(weatherObservation, vehicleType);

        assertThat(result).isEqualByComparingTo(expectedFee);
    }

    @ParameterizedTest(name = "[{index}] vehicleType={0}, phenomenon={1}")
    @MethodSource("forbiddenWeatherPhenomenonCases")
    void calculate_withScooterAndBikeAndForbiddenWeatherPhenomenon_shouldThrowException(
            VehicleType vehicleType,
            String phenomenon
    ) {
        WeatherObservation weatherObservation = weatherObservation(
                BigDecimal.ZERO,
                BigDecimal.ONE,
                phenomenon
        );

        assertThatThrownBy(() -> weatherExtraFeeService.calculate(weatherObservation, vehicleType))
                .isInstanceOf(ForbiddenVehicleUsageException.class)
                .hasMessage("Usage of selected vehicle type is forbidden");
    }

    private static Stream<Arguments> temperatureExtraFeeCases() {
        return Stream.of(
                arguments(VehicleType.SCOOTER, BigDecimal.valueOf(-5), BigDecimal.valueOf(0.5)),
                arguments(VehicleType.BIKE, BigDecimal.valueOf(-5), BigDecimal.valueOf(0.5)),
                arguments(VehicleType.SCOOTER, BigDecimal.valueOf(-11), BigDecimal.ONE),
                arguments(VehicleType.BIKE, BigDecimal.valueOf(-11), BigDecimal.ONE),
                arguments(VehicleType.SCOOTER, BigDecimal.ZERO, BigDecimal.ZERO),
                arguments(VehicleType.BIKE, BigDecimal.ZERO, BigDecimal.ZERO),
                arguments(VehicleType.SCOOTER, BigDecimal.valueOf(-10), BigDecimal.valueOf(0.5)),
                arguments(VehicleType.BIKE, BigDecimal.valueOf(-10), BigDecimal.valueOf(0.5)),
                arguments(VehicleType.SCOOTER, null, BigDecimal.ZERO),
                arguments(VehicleType.BIKE, null, BigDecimal.ZERO)
        );
    }

    private static Stream<Arguments> bikeWindExtraFeeCases() {
        return Stream.of(
                arguments(BigDecimal.valueOf(11), BigDecimal.valueOf(0.5)),
                arguments(BigDecimal.valueOf(10), BigDecimal.valueOf(0.5)),
                arguments(BigDecimal.valueOf(20), BigDecimal.valueOf(0.5)),
                arguments(BigDecimal.valueOf(1), BigDecimal.ZERO),
                arguments(null, BigDecimal.ZERO)
        );
    }

    private static Stream<Arguments> forbiddenBikeWindSpeedCases() {
        return Stream.of(
                arguments(BigDecimal.valueOf(21))
        );
    }

    private static Stream<Arguments> weatherPhenomenonExtraFeeCases() {
        return Stream.of(
                arguments(VehicleType.SCOOTER, " SNOW ", BigDecimal.ONE),
                arguments(VehicleType.BIKE, " SNOW ", BigDecimal.ONE),
                arguments(VehicleType.SCOOTER, " SLEET    ", BigDecimal.ONE),
                arguments(VehicleType.BIKE, " SLEET    ", BigDecimal.ONE),
                arguments(VehicleType.SCOOTER, "   RAIN ", BigDecimal.valueOf(0.5)),
                arguments(VehicleType.BIKE, "   RAIN ", BigDecimal.valueOf(0.5)),
                arguments(VehicleType.SCOOTER, "   ShOwEr ", BigDecimal.valueOf(0.5)),
                arguments(VehicleType.BIKE, "   ShOwEr ", BigDecimal.valueOf(0.5)),
                arguments(VehicleType.SCOOTER, " ", BigDecimal.ZERO),
                arguments(VehicleType.BIKE, " ", BigDecimal.ZERO),
                arguments(VehicleType.SCOOTER, null, BigDecimal.ZERO),
                arguments(VehicleType.BIKE, null, BigDecimal.ZERO)
        );
    }

    private static Stream<Arguments> forbiddenWeatherPhenomenonCases() {
        return Stream.of(
                arguments(VehicleType.SCOOTER, " glaze "),
                arguments(VehicleType.BIKE, " glaze "),
                arguments(VehicleType.SCOOTER, "thunder"),
                arguments(VehicleType.BIKE, "thunder"),
                arguments(VehicleType.SCOOTER, "Hail"),
                arguments(VehicleType.BIKE, "Hail")
        );
    }

    private WeatherObservation weatherObservation(
            BigDecimal airTemperature,
            BigDecimal windSpeed,
            String weatherPhenomenon
    ) {
        return new WeatherObservation(
                "Test Station",
                "12345",
                City.TALLINN,
                airTemperature,
                windSpeed,
                weatherPhenomenon,
                LocalDateTime.of(2026, 3, 20, 12, 0),
                LocalDateTime.of(2026, 3, 20, 12, 1)
        );
    }
}
