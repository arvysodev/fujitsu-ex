package ee.trialtask.delivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryFeeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Fee Calculator API")
                        .version("1.0.0")
                        .description("REST API for calculating courier delivery fees based on city, vehicle type, and weather data."));
    }
}
