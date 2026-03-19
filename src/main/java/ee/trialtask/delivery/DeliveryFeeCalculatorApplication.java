package ee.trialtask.delivery;

import ee.trialtask.delivery.weather.config.WeatherProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties(WeatherProperties.class)
@EnableScheduling
@SpringBootApplication
public class DeliveryFeeCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryFeeCalculatorApplication.class, args);
	}

}
