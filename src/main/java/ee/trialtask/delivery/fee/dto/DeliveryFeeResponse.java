package ee.trialtask.delivery.fee.dto;

import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.weather.domain.City;

import java.math.BigDecimal;

public record DeliveryFeeResponse(
        City city,
        VehicleType vehicleType,
        BigDecimal fee
) {
}
