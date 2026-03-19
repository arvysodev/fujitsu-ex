package ee.trialtask.delivery.fee.controller;

import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.fee.dto.DeliveryFeeResponse;
import ee.trialtask.delivery.fee.service.DeliveryFeeCalculationService;
import ee.trialtask.delivery.weather.domain.City;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery-fee")
public class DeliveryFeeController {

    private final DeliveryFeeCalculationService deliveryFeeCalculationService;

    public DeliveryFeeController(DeliveryFeeCalculationService deliveryFeeCalculationService) {
        this.deliveryFeeCalculationService = deliveryFeeCalculationService;
    }

    @GetMapping()
    public DeliveryFeeResponse getDeliveryFee(
            @RequestParam City city,
            @RequestParam VehicleType vehicleType
    ) {
        return deliveryFeeCalculationService.calculate(city, vehicleType);
    }
}
