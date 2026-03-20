package ee.trialtask.delivery.fee.controller;

import ee.trialtask.delivery.fee.domain.VehicleType;
import ee.trialtask.delivery.fee.dto.DeliveryFeeResponse;
import ee.trialtask.delivery.fee.service.DeliveryFeeCalculationService;
import ee.trialtask.delivery.weather.domain.City;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Delivery Fee", description = "Operations for calculating courier delivery fee")
@RestController
@RequestMapping("/api/v1/delivery-fee")
public class DeliveryFeeController {

    private final DeliveryFeeCalculationService deliveryFeeCalculationService;

    public DeliveryFeeController(DeliveryFeeCalculationService deliveryFeeCalculationService) {
        this.deliveryFeeCalculationService = deliveryFeeCalculationService;
    }

    @Operation(
            summary = "Calculate delivery fee",
            description = "Calculates the total delivery fee for the given city and vehicle type using the latest available weather observation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery fee calculated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameter or vehicle usage forbidden due to weather",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Weather data for the requested city was not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content
            )
    })
    @GetMapping
    public DeliveryFeeResponse getDeliveryFee(
            @Parameter(description = "City for delivery fee calculation", example = "TALLINN")
            @RequestParam City city,
            @Parameter(description = "Vehicle type used for delivery", example = "CAR")
            @RequestParam VehicleType vehicleType
    ) {
        return deliveryFeeCalculationService.calculate(city, vehicleType);
    }
}
