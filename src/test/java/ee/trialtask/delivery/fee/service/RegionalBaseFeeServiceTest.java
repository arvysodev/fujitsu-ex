package ee.trialtask.delivery.fee.service;

import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.weather.domain.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RegionalBaseFeeServiceTest {

    private RegionalBaseFeeService regionalBaseFeeService;

    @BeforeEach
    void setUp() {
        regionalBaseFeeService = new RegionalBaseFeeService();
    }

    @ParameterizedTest(name = "[{index}] {0} / {1} -> {2}")
    @MethodSource("regionalBaseFeeCases")
    void calculate_shouldReturnCorrectRegionalBaseFee(
            City city,
            VehicleType vehicleType,
            BigDecimal expectedFee
    ) {
        BigDecimal actualFee = regionalBaseFeeService.calculate(city, vehicleType);

        assertThat(actualFee).isEqualByComparingTo(expectedFee);
    }

    private static Stream<Arguments> regionalBaseFeeCases() {
        return Stream.of(
                arguments(City.TALLINN, VehicleType.CAR, BigDecimal.valueOf(4.0)),
                arguments(City.TALLINN, VehicleType.SCOOTER, BigDecimal.valueOf(3.5)),
                arguments(City.TALLINN, VehicleType.BIKE, BigDecimal.valueOf(3.0)),
                arguments(City.TARTU, VehicleType.CAR, BigDecimal.valueOf(3.5)),
                arguments(City.TARTU, VehicleType.SCOOTER, BigDecimal.valueOf(3.0)),
                arguments(City.TARTU, VehicleType.BIKE, BigDecimal.valueOf(2.5)),
                arguments(City.PARNU, VehicleType.CAR, BigDecimal.valueOf(3.0)),
                arguments(City.PARNU, VehicleType.SCOOTER, BigDecimal.valueOf(2.5)),
                arguments(City.PARNU, VehicleType.BIKE, BigDecimal.valueOf(2.0))
        );
    }
}
