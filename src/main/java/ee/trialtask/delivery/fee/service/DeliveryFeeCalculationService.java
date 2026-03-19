package ee.trialtask.delivery.fee.service;

import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.exception.WeatherObservationNotFoundException;
import ee.trialtask.delivery.fee.dto.DeliveryFeeResponse;
import ee.trialtask.delivery.weather.domain.City;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import ee.trialtask.delivery.weather.repository.WeatherObservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DeliveryFeeCalculationService {

    private final RegionalBaseFeeService regionalBaseFeeService;
    private final WeatherExtraFeeService weatherExtraFeeService;
    private final WeatherObservationRepository weatherObservationRepository;

    public DeliveryFeeCalculationService(
            RegionalBaseFeeService regionalBaseFeeService,
            WeatherExtraFeeService weatherExtraFeeService,
            WeatherObservationRepository weatherObservationRepository
    ) {
        this.regionalBaseFeeService = regionalBaseFeeService;
        this.weatherExtraFeeService = weatherExtraFeeService;
        this.weatherObservationRepository = weatherObservationRepository;
    }

    @Transactional(readOnly = true)
    public DeliveryFeeResponse calculate(City city, VehicleType vehicleType) {
        WeatherObservation weatherObservation = weatherObservationRepository
                .findTopByCityOrderByObservationTimestampDesc(city)
                .orElseThrow(() -> new WeatherObservationNotFoundException(city));

        BigDecimal regionalBaseFee = regionalBaseFeeService.calculate(city, vehicleType);
        BigDecimal weatherExtraFee = weatherExtraFeeService.calculate(weatherObservation, vehicleType);
        BigDecimal totalFee = regionalBaseFee.add(weatherExtraFee);

        return new DeliveryFeeResponse(city, vehicleType, totalFee);
    }
}
