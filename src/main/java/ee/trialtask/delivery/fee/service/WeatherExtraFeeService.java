package ee.trialtask.delivery.fee.service;

import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.exception.ForbiddenVehicleUsageException;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WeatherExtraFeeService {

    private static final BigDecimal ZERO_FEE = BigDecimal.ZERO;
    private static final BigDecimal HALF_EURO = BigDecimal.valueOf(0.5);
    private static final BigDecimal ONE_EURO = BigDecimal.ONE;

    public BigDecimal calculate(WeatherObservation weatherObservation, VehicleType vehicleType) {
        if (vehicleType == VehicleType.CAR) {
            return ZERO_FEE;
        }

        BigDecimal airTemperatureFee = calculateAirTemperatureFee(weatherObservation);
        BigDecimal windSpeedFee = calculateWindSpeedFee(weatherObservation, vehicleType);
        BigDecimal weatherPhenomenonFee = calculateWeatherPhenomenonFee(weatherObservation);

        return airTemperatureFee
                .add(windSpeedFee)
                .add(weatherPhenomenonFee);
    }

    private BigDecimal calculateAirTemperatureFee(WeatherObservation weatherObservation) {
        BigDecimal airTemperature = weatherObservation.getAirTemperature();
        if (airTemperature == null) {
            return ZERO_FEE;
        }

        if (airTemperature.compareTo(BigDecimal.valueOf(-10)) < 0) {
            return ONE_EURO;
        }

        if (airTemperature.compareTo(BigDecimal.valueOf(0)) < 0) {
            return HALF_EURO;
        }

        return ZERO_FEE;
    }

    private BigDecimal calculateWindSpeedFee(WeatherObservation weatherObservation, VehicleType vehicleType) {
        if (vehicleType != VehicleType.BIKE) {
            return ZERO_FEE;
        }

        BigDecimal windSpeed = weatherObservation.getWindSpeed();
        if (windSpeed == null ) {
            return ZERO_FEE;
        }

        if (windSpeed.compareTo(BigDecimal.valueOf(20)) > 0) {
            throw new ForbiddenVehicleUsageException("Usage of selected vehicle type is forbidden");
        }

        if (windSpeed.compareTo(BigDecimal.valueOf(10)) >= 0) {
            return HALF_EURO;
        }

        return ZERO_FEE;
    }

    private BigDecimal calculateWeatherPhenomenonFee(WeatherObservation weatherObservation) {
        String phenomenon = weatherObservation.getWeatherPhenomenon();
        if (phenomenon == null) {
            return ZERO_FEE;
        }

        String normalizedPhenomenon = phenomenon.trim().toLowerCase();

        if (containsForbiddenPhenomenon(normalizedPhenomenon)) {
            throw new ForbiddenVehicleUsageException("Usage of selected vehicle type is forbidden");
        }

        if (containsSnowPhenomenon(normalizedPhenomenon)) {
            return ONE_EURO;
        }

        if (containsRainPhenomenon(normalizedPhenomenon)) {
            return HALF_EURO;
        }

        return ZERO_FEE;
    }

    private boolean containsForbiddenPhenomenon(String phenomenon) {
        return phenomenon.contains("glaze")
                || phenomenon.contains("hail")
                || phenomenon.contains("thunder");
    }

    private boolean containsSnowPhenomenon(String phenomenon) {
        return phenomenon.contains("snow") || phenomenon.contains("sleet");
    }

    private boolean containsRainPhenomenon(String phenomenon) {
        return phenomenon.contains("rain") || phenomenon.contains("shower");
    }
}
