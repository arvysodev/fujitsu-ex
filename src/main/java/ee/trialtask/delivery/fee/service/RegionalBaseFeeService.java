package ee.trialtask.delivery.fee.service;

import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.weather.domain.City;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RegionalBaseFeeService {

    /**
     * Returns the regional base fee for the given city and vehicle type.
     *
     * @param city the city where the delivery takes place
     * @param vehicleType the vehicle type used for delivery
     * @return the regional base fee
     */
    public BigDecimal calculate(City city, VehicleType vehicleType) {
        return switch (city) {
            case TALLINN -> calculateTallinnBaseFee(vehicleType);
            case TARTU -> calculateTartuBaseFee(vehicleType);
            case PARNU -> calculateParnuBaseFee(vehicleType);
        };
    }

    private BigDecimal calculateTallinnBaseFee(VehicleType vehicleType) {
        return switch (vehicleType) {
            case CAR -> BigDecimal.valueOf(4.0);
            case SCOOTER -> BigDecimal.valueOf(3.5);
            case BIKE -> BigDecimal.valueOf(3.0);
        };
    }

    private BigDecimal calculateTartuBaseFee(VehicleType vehicleType) {
        return switch (vehicleType) {
            case CAR -> BigDecimal.valueOf(3.5);
            case SCOOTER -> BigDecimal.valueOf(3.0);
            case BIKE -> BigDecimal.valueOf(2.5);
        };
    }

    private BigDecimal calculateParnuBaseFee(VehicleType vehicleType) {
        return switch (vehicleType) {
            case CAR -> BigDecimal.valueOf(3.0);
            case SCOOTER -> BigDecimal.valueOf(2.5);
            case BIKE -> BigDecimal.valueOf(2.0);
        };
    }
}
