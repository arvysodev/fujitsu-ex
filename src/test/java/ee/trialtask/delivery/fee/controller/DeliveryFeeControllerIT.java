package ee.trialtask.delivery.fee.controller;

import ee.trialtask.delivery.weather.domain.City;
import ee.trialtask.delivery.weather.domain.WeatherObservation;
import ee.trialtask.delivery.weather.repository.WeatherObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeliveryFeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherObservationRepository weatherObservationRepository;

    @BeforeEach
    void setUp() {
        weatherObservationRepository.deleteAll();

        weatherObservationRepository.save(new WeatherObservation(
                "Tallinn-Harku",
                "26038",
                City.TALLINN,
                BigDecimal.valueOf(2.0),
                BigDecimal.valueOf(4.0),
                "Clear",
                LocalDateTime.of(2026, 3, 20, 12, 0),
                LocalDateTime.of(2026, 3, 20, 12, 1)
        ));
    }

    @Test
    void calculate_withoutDateTime_shouldReturnDeliveryFeeUsingLatestObservation() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicleType").value("CAR"))
                .andExpect(jsonPath("$.fee").value(4.0));
    }

    @Test
    void calculate_withDateTime_shouldReturnDeliveryFeeUsingHistoricalObservation() throws Exception {
        weatherObservationRepository.save(new WeatherObservation(
                "Tallinn-Harku",
                "26038",
                City.TALLINN,
                BigDecimal.valueOf(-12.0),
                BigDecimal.valueOf(4.0),
                "Snow",
                LocalDateTime.of(2026, 3, 20, 13, 0),
                LocalDateTime.of(2026, 3, 20, 13, 1)
        ));

        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "SCOOTER")
                        .param("dateTime", "2026-03-20T12:30:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicleType").value("SCOOTER"))
                .andExpect(jsonPath("$.fee").value(3.5));
    }

    @Test
    void calculate_whenWeatherObservationNotFound_shouldReturnNotFound() throws Exception {
        weatherObservationRepository.deleteAll();

        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Weather Observation Not Found"))
                .andExpect(jsonPath("$.detail").value("No weather observation found for city: TALLINN"));
    }

    @Test
    void calculate_whenVehicleUsageIsForbiddenDueToWind_shouldReturnBadRequest() throws Exception {
        weatherObservationRepository.deleteAll();

        weatherObservationRepository.save(new WeatherObservation(
                "Tallinn-Harku",
                "26038",
                City.TALLINN,
                BigDecimal.valueOf(2.0),
                BigDecimal.valueOf(21.0),
                "Clear",
                LocalDateTime.of(2026, 3, 20, 12, 0),
                LocalDateTime.of(2026, 3, 20, 12, 1)
        ));

        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Vehicle Usage Forbidden"))
                .andExpect(jsonPath("$.detail").value("Usage of selected vehicle type is forbidden"));
    }

    @Test
    void calculate_whenVehicleUsageIsForbiddenDueToPhenomenon_shouldReturnBadRequest() throws Exception {
        weatherObservationRepository.deleteAll();

        weatherObservationRepository.save(new WeatherObservation(
                "Tallinn-Harku",
                "26038",
                City.TALLINN,
                BigDecimal.valueOf(2.0),
                BigDecimal.valueOf(4.0),
                "Hail",
                LocalDateTime.of(2026, 3, 20, 12, 0),
                LocalDateTime.of(2026, 3, 20, 12, 1)
        ));

        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "SCOOTER"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Vehicle Usage Forbidden"))
                .andExpect(jsonPath("$.detail").value("Usage of selected vehicle type is forbidden"));
    }

    @Test
    void calculate_withInvalidCity_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "INVALID")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Invalid Request Parameter"))
                .andExpect(jsonPath("$.detail").value("Invalid value for request parameter 'city: INVALID'."));
    }

    @Test
    void calculate_withInvalidVehicleType_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "PLANE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Invalid Request Parameter"))
                .andExpect(jsonPath("$.detail").value("Invalid value for request parameter 'vehicleType: PLANE'."));
    }

    @Test
    void calculate_whenVehicleTypeIsMissing_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Missing Request Parameter"))
                .andExpect(jsonPath("$.detail").value("Missing required request parameter: 'vehicleType'."));
    }

    @Test
    void calculate_whenDateTimeIsBeforeAnyObservation_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-fee")
                        .param("city", "TALLINN")
                        .param("vehicleType", "CAR")
                        .param("dateTime", "2026-03-19T11:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Weather Observation Not Found"))
                .andExpect(jsonPath("$.detail").value("No weather observation found for city: TALLINN"));
    }
}
